package com.coworkflex.service;

import com.coworkflex.dto.Dtos.ReservationRequestDTO;
import com.coworkflex.entity.*;
import com.coworkflex.exception.BusinessException;
import com.coworkflex.exception.ConflictException;
import com.coworkflex.repository.DeskRepository;
import com.coworkflex.repository.ReservationRepository;
import com.coworkflex.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — ReservationService")
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private DeskRepository deskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Desk desk;
    private User user;
    private Space space;

    @BeforeEach
    void setUp() {
        space = Space.builder()
            .id(1L).name("CoWork Plateau")
            .city("Abidjan").address("12 Avenue Noguès")
            .capacity(40).build();

        desk = Desk.builder()
            .id(1L).label("Bureau A1")
            .type(DeskType.OPEN_SPACE)
            .pricePerHour(new BigDecimal("2500"))
            .available(true)
            .space(space)
            .build();

        user = User.builder()
            .id(1L).fullName("Kouassi Jean")
            .email("jean@gmail.com")
            .password("hashed")
            .role(User.Role.USER)
            .build();
    }

    // ══════════════════════════════════════════════════════════
    //  TEST 1 — Réservation créée avec succès (pas de conflit)
    // ══════════════════════════════════════════════════════════
    @Test
    @DisplayName("✅ Doit créer une réservation quand le créneau est libre")
    void shouldCreateReservationWhenSlotIsAvailable() {
        // ARRANGE
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end   = start.plusHours(3);

        ReservationRequestDTO req = new ReservationRequestDTO(
            1L, start, end, "Notes test");

        // Aucun conflit → countOverlapping retourne 0
        when(deskRepository.findById(1L))
            .thenReturn(Optional.of(desk));
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
        when(reservationRepository.countOverlapping(1L, start, end))
            .thenReturn(0L);
        when(reservationRepository.save(any(Reservation.class)))
            .thenAnswer(inv -> {
                Reservation r = inv.getArgument(0);
                // Simuler l'ID auto-généré
                return Reservation.builder()
                    .id(42L).desk(desk).user(user)
                    .startDateTime(start).endDateTime(end)
                    .totalPrice(new BigDecimal("7500")) // 3h × 2500
                    .status(ReservationStatus.CONFIRMED)
                    .createdAt(LocalDateTime.now())
                    .build();
            });

        // ACT
        var result = reservationService.create(req, 1L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(42L);
        assertThat(result.status()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(result.totalPrice())
            .isEqualByComparingTo(new BigDecimal("7500")); // 3h × 2500

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    // ══════════════════════════════════════════════════════════
    //  TEST 2 — Conflit de créneau → ConflictException
    // ══════════════════════════════════════════════════════════
    @Test
    @DisplayName("❌ Doit lever ConflictException quand le créneau chevauche une réservation existante")
    void shouldThrowConflictExceptionWhenSlotOverlaps() {
        // ARRANGE
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end   = start.plusHours(3);

        ReservationRequestDTO req = new ReservationRequestDTO(
            1L, start, end, null);

        when(deskRepository.findById(1L))
            .thenReturn(Optional.of(desk));
        // Conflit détecté → countOverlapping retourne 1
        when(reservationRepository.countOverlapping(1L, start, end))
            .thenReturn(1L);

        // ACT & ASSERT
        assertThatThrownBy(() -> reservationService.create(req, 1L))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("déjà réservé");

        // Le save ne doit jamais être appelé
        verify(reservationRepository, never()).save(any());
    }

    // ══════════════════════════════════════════════════════════
    //  TEST 3 — Annulation impossible à moins de 24h
    // ══════════════════════════════════════════════════════════
    @Test
    @DisplayName("❌ Doit lever BusinessException si annulation à moins de 24h du début")
    void shouldThrowBusinessExceptionWhenCancellingWithinDeadline() {
        // ARRANGE
        // La réservation commence dans 10h → moins de 24h → annulation bloquée
        LocalDateTime start = LocalDateTime.now().plusHours(10);
        LocalDateTime end   = start.plusHours(2);

        Reservation reservation = Reservation.builder()
            .id(99L).desk(desk).user(user)
            .startDateTime(start).endDateTime(end)
            .status(ReservationStatus.CONFIRMED)
            .totalPrice(new BigDecimal("5000"))
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        when(reservationRepository.findById(99L))
            .thenReturn(Optional.of(reservation));

        // ACT & ASSERT
        assertThatThrownBy(() -> reservationService.cancel(99L, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("moins de 24h");

        // Le save ne doit jamais être appelé (pas d'annulation)
        verify(reservationRepository, never()).save(any());
    }

    // ══════════════════════════════════════════════════════════
    //  TEST BONUS — Date de début dans le passé
    // ══════════════════════════════════════════════════════════
    @Test
    @DisplayName("❌ Doit lever BusinessException si start est dans le passé")
    void shouldThrowBusinessExceptionWhenStartIsInPast() {
        // ARRANGE
        LocalDateTime start = LocalDateTime.now().minusHours(1); // Passé !
        LocalDateTime end   = LocalDateTime.now().plusHours(1);

        ReservationRequestDTO req = new ReservationRequestDTO(
            1L, start, end, null);

        when(deskRepository.findById(1L))
            .thenReturn(Optional.of(desk));

        // ACT & ASSERT
        assertThatThrownBy(() -> reservationService.create(req, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("futur");

        verify(reservationRepository, never()).save(any());
    }

    // ══════════════════════════════════════════════════════════
    //  TEST BONUS 2 — start après end → BusinessException
    // ══════════════════════════════════════════════════════════
    @Test
    @DisplayName("❌ Doit lever BusinessException si start >= end")
    void shouldThrowBusinessExceptionWhenStartAfterEnd() {
        // ARRANGE
        LocalDateTime start = LocalDateTime.now().plusHours(5);
        LocalDateTime end   = LocalDateTime.now().plusHours(2); // end < start !

        ReservationRequestDTO req = new ReservationRequestDTO(
            1L, start, end, null);

        when(deskRepository.findById(1L))
            .thenReturn(Optional.of(desk));

        // ACT & ASSERT
        assertThatThrownBy(() -> reservationService.create(req, 1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("antérieure");

        verify(reservationRepository, never()).save(any());
    }
}
