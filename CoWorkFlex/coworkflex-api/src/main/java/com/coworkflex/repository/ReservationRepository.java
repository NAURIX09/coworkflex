package com.coworkflex.repository;

import com.coworkflex.entity.Reservation;
import com.coworkflex.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // ─── Historique utilisateur ───────────────────────────────
    List<Reservation> findByUserIdOrderByStartDateTimeDesc(Long userId);

    List<Reservation> findByUserIdAndStatusOrderByStartDateTimeDesc(
        Long userId, ReservationStatus status
    );

    // ─── ALGORITHME ANTI-CHEVAUCHEMENT ────────────────────────
    // Compte les réservations CONFIRMED sur le même poste
    // qui chevauchent la plage [start, end]
    //
    // Condition de chevauchement :
    //   existingStart < newEnd  AND  existingEnd > newStart
    //
    // Couvre tous les cas :
    //   [---existing---]           → total overlap
    //      [---new---]
    //
    //   [---existing---]           → existing starts before
    //         [---new---]
    //
    //      [---existing---]        → existing ends after
    //   [---new---]
    //
    //   [------existing------]     → existing contains new
    //      [---new---]
    @Query("""
        SELECT COUNT(r) FROM Reservation r
        WHERE r.desk.id    = :deskId
          AND r.status     = 'CONFIRMED'
          AND r.startDateTime < :end
          AND r.endDateTime   > :start
    """)
    Long countOverlapping(
        @Param("deskId") Long deskId,
        @Param("start")  LocalDateTime start,
        @Param("end")    LocalDateTime end
    );

    // Même requête avec exclusion d'une réservation (pour modification)
    @Query("""
        SELECT COUNT(r) FROM Reservation r
        WHERE r.desk.id    = :deskId
          AND r.id        != :excludeId
          AND r.status     = 'CONFIRMED'
          AND r.startDateTime < :end
          AND r.endDateTime   > :start
    """)
    Long countOverlappingExcluding(
        @Param("deskId")    Long deskId,
        @Param("excludeId") Long excludeId,
        @Param("start")     LocalDateTime start,
        @Param("end")       LocalDateTime end
    );

    // ─── Requêtes admin ───────────────────────────────────────
    // Toutes les réservations d'un espace (pour tableau de bord admin)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.desk.space.id = :spaceId
        ORDER BY r.startDateTime DESC
    """)
    List<Reservation> findBySpaceId(@Param("spaceId") Long spaceId);

    // Réservations actives (futures + CONFIRMED)
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.user.id    = :userId
          AND r.status     = 'CONFIRMED'
          AND r.endDateTime > :now
        ORDER BY r.startDateTime ASC
    """)
    List<Reservation> findUpcomingByUserId(
        @Param("userId") Long userId,
        @Param("now")    LocalDateTime now
    );

    // Réservations passées d'un utilisateur
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.user.id    = :userId
          AND r.endDateTime <= :now
        ORDER BY r.startDateTime DESC
    """)
    List<Reservation> findPastByUserId(
        @Param("userId") Long userId,
        @Param("now")    LocalDateTime now
    );
}
