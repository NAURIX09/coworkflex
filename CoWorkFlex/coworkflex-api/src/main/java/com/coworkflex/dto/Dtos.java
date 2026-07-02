package com.coworkflex.dto;

import com.coworkflex.entity.DeskType;
import com.coworkflex.entity.ReservationStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ═══════════════════════════════════════════════════════════
//  THETDESIGN — DTOs CoWork-Flex
// ═══════════════════════════════════════════════════════════

// ─── SPACE ───────────────────────────────────────────────────
public class Dtos {

    // Réponse : espace de coworking
    public record SpaceDTO(
        Long id,
        String name,
        String city,
        String address,
        Integer capacity,
        String description,
        String imageUrl,
        Double rating,
        int totalDesks
    ) {}

    // Réponse : poste de travail
    public record DeskDTO(
        Long id,
        String label,
        DeskType type,
        BigDecimal pricePerHour,
        String amenities,
        Boolean available,
        Long spaceId,
        String spaceName
    ) {}

    // ─── AUTH ─────────────────────────────────────────────────
    public record RegisterRequest(
        @NotBlank(message = "Le nom complet est requis")
        String fullName,

        @Email(message = "Email invalide")
        @NotBlank(message = "L'email est requis")
        String email,

        @NotBlank(message = "Le mot de passe est requis")
        @Size(min = 6, message = "Mot de passe : 6 caractères minimum")
        String password
    ) {}

    public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
    ) {}

    public record AuthResponse(
        String token,
        Long userId,
        String fullName,
        String email,
        String role
    ) {}

    // ─── RESERVATION ──────────────────────────────────────────
    public record ReservationRequestDTO(
        @NotNull(message = "L'identifiant du poste est requis")
        Long deskId,

        @NotNull(message = "La date de début est requise")
        @Future(message = "La date de début doit être dans le futur")
        LocalDateTime startDateTime,

        @NotNull(message = "La date de fin est requise")
        LocalDateTime endDateTime,

        String notes
    ) {}

    public record ReservationDTO(
        Long id,
        Long deskId,
        String deskLabel,
        DeskType deskType,
        String spaceName,
        String spaceCity,
        Long userId,
        String userFullName,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        ReservationStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        String notes
    ) {}
}
