package com.coworkflex.repository;

import com.coworkflex.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {

    // Filtre par ville (insensible à la casse)
    List<Space> findByCityIgnoreCase(String city);

    // Filtre par capacité minimale
    List<Space> findByCapacityGreaterThanEqual(Integer minCapacity);

    // Filtre combiné ville + capacité
    @Query("""
        SELECT s FROM Space s
        WHERE (:city IS NULL OR LOWER(s.city) = LOWER(:city))
          AND (:minCapacity IS NULL OR s.capacity >= :minCapacity)
        ORDER BY s.name
    """)
    List<Space> findWithFilters(
        @Param("city") String city,
        @Param("minCapacity") Integer minCapacity
    );

    // Liste des villes disponibles (pour le filtre dropdown)
    @Query("SELECT DISTINCT s.city FROM Space s ORDER BY s.city")
    List<String> findAllCities();
}
