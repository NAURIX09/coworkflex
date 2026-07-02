package com.coworkflex.repository;

import com.coworkflex.entity.Desk;
import com.coworkflex.entity.DeskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeskRepository extends JpaRepository<Desk, Long> {

    // Tous les postes d'un espace
    List<Desk> findBySpaceId(Long spaceId);

    // Postes d'un espace filtrés par type
    List<Desk> findBySpaceIdAndType(Long spaceId, DeskType type);

    // Postes disponibles dans un espace pour un créneau donné
    // Un poste est disponible s'il n'a aucune réservation CONFIRMED
    // qui chevauche la plage [start, end]
    @Query("""
        SELECT d FROM Desk d
        WHERE d.space.id = :spaceId
          AND d.available = true
          AND NOT EXISTS (
              SELECT r FROM Reservation r
              WHERE r.desk = d
                AND r.status = 'CONFIRMED'
                AND r.startDateTime < :end
                AND r.endDateTime   > :start
          )
    """)
    List<Desk> findAvailableDesks(
        @Param("spaceId") Long spaceId,
        @Param("start")   LocalDateTime start,
        @Param("end")     LocalDateTime end
    );

    // Postes disponibles filtrés par type ET créneau
    @Query("""
        SELECT d FROM Desk d
        WHERE d.space.id = :spaceId
          AND d.available = true
          AND (:type IS NULL OR d.type = :type)
          AND NOT EXISTS (
              SELECT r FROM Reservation r
              WHERE r.desk = d
                AND r.status = 'CONFIRMED'
                AND r.startDateTime < :end
                AND r.endDateTime   > :start
          )
    """)
    List<Desk> findAvailableDesksByType(
        @Param("spaceId") Long spaceId,
        @Param("type")    DeskType type,
        @Param("start")   LocalDateTime start,
        @Param("end")     LocalDateTime end
    );
}
