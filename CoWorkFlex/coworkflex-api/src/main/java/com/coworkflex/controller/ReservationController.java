package com.coworkflex.controller;

import com.coworkflex.dto.Dtos.ReservationDTO;
import com.coworkflex.dto.Dtos.ReservationRequestDTO;
import com.coworkflex.entity.User;
import com.coworkflex.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Réservations", description = "Créer, consulter et annuler des réservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @Operation(summary = "Créer une réservation")
    public ResponseEntity<ReservationDTO> create(
            @Valid @RequestBody ReservationRequestDTO req,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(reservationService.create(req, currentUser.getId()));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Historique des réservations d'un utilisateur")
    public ResponseEntity<List<ReservationDTO>> getByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reservationService.findByUser(userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annuler une réservation")
    public ResponseEntity<Map<String, String>> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        reservationService.cancel(id, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Réservation annulée avec succès."));
    }

    @GetMapping("/space/{spaceId}")
    @Operation(summary = "Réservations d'un espace (admin)")
    public ResponseEntity<List<ReservationDTO>> getBySpace(
            @PathVariable Long spaceId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reservationService.findBySpace(spaceId));
    }
}
