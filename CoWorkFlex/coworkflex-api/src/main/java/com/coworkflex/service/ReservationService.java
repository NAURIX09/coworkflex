package com.coworkflex.service;

import com.coworkflex.dto.Dtos.ReservationDTO;
import com.coworkflex.dto.Dtos.ReservationRequestDTO;
import com.coworkflex.entity.*;
import com.coworkflex.exception.BusinessException;
import com.coworkflex.exception.ConflictException;
import com.coworkflex.exception.ForbiddenException;
import com.coworkflex.exception.NotFoundException;
import com.coworkflex.repository.DeskRepository;
import com.coworkflex.repository.ReservationRepository;
import com.coworkflex.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final DeskRepository deskRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              DeskRepository deskRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.deskRepository = deskRepository;
        this.userRepository = userRepository;
    }

    // ─── POST /api/reservations ───────────────────────────────
    public ReservationDTO create(ReservationRequestDTO req, Long userId) {

        if (!req.startDateTime().isBefore(req.endDateTime())) {
            throw new BusinessException("La date de début doit être antérieure à la date de fin.");
        }

        if (req.startDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("La date de début doit être dans le futur.");
        }

        Desk desk = deskRepository.findById(req.deskId())
            .orElseThrow(() -> new NotFoundException("Poste introuvable : " + req.deskId()));

        if (!desk.getAvailable()) {
            throw new BusinessException("Ce poste n'est pas disponible à la réservation.");
        }

        long conflicts = reservationRepository.countOverlapping(
            req.deskId(), req.startDateTime(), req.endDateTime());

        if (conflicts > 0) {
            throw new ConflictException(
                "Ce poste est déjà réservé sur ce créneau. Veuillez choisir un autre horaire.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Utilisateur introuvable : " + userId));

        long hours = ChronoUnit.HOURS.between(req.startDateTime(), req.endDateTime());
        if (hours < 1) {
            throw new BusinessException("La durée minimale de réservation est d'1 heure.");
        }

        BigDecimal totalPrice = desk.getPricePerHour().multiply(BigDecimal.valueOf(hours));

        Reservation reservation = Reservation.builder()
            .desk(desk).user(user)
            .startDateTime(req.startDateTime()).endDateTime(req.endDateTime())
            .totalPrice(totalPrice).notes(req.notes())
            .status(ReservationStatus.CONFIRMED)
            .build();

        Reservation saved = reservationRepository.save(reservation);
        log.info("Réservation créée : id={}, desk={}, user={}", saved.getId(), desk.getLabel(), user.getEmail());

        return toDTO(saved);
    }

    // ─── GET /api/reservations/user/{userId} ──────────────────
    @Transactional(readOnly = true)
    public List<ReservationDTO> findByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Utilisateur introuvable : " + userId);
        }
        return reservationRepository
            .findByUserIdOrderByStartDateTimeDesc(userId)
            .stream().map(this::toDTO).toList();
    }

    // ─── DELETE /api/reservations/{id} ───────────────────────
    public void cancel(Long reservationId, Long userId) {

        Reservation res = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new NotFoundException("Réservation introuvable : " + reservationId));

        if (!res.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Vous n'êtes pas autorisé à annuler cette réservation.");
        }

        if (res.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException("Cette réservation est déjà annulée.");
        }

        LocalDateTime deadline = res.getStartDateTime().minus(24, ChronoUnit.HOURS);
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new BusinessException("Annulation impossible : la réservation commence dans moins de 24h.");
        }

        res.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(res);
        log.info("Réservation annulée : id={}, user={}", reservationId, userId);
    }

    // ─── Admin ────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ReservationDTO> findBySpace(Long spaceId) {
        return reservationRepository.findBySpaceId(spaceId)
            .stream().map(this::toDTO).toList();
    }

    // ─── Mapper ───────────────────────────────────────────────
    private ReservationDTO toDTO(Reservation r) {
        Desk d = r.getDesk();
        User u = r.getUser();
        return new ReservationDTO(
            r.getId(),
            d.getId(), d.getLabel(), d.getType(),
            d.getSpace() != null ? d.getSpace().getName() : null,
            d.getSpace() != null ? d.getSpace().getCity() : null,
            u.getId(), u.getFullName(),
            r.getStartDateTime(), r.getEndDateTime(),
            r.getStatus(), r.getTotalPrice(),
            r.getCreatedAt(), r.getNotes()
        );
    }
}
