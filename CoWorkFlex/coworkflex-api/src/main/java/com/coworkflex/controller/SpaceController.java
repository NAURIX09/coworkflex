package com.coworkflex.controller;

import com.coworkflex.dto.Dtos.DeskDTO;
import com.coworkflex.dto.Dtos.SpaceDTO;
import com.coworkflex.entity.DeskType;
import com.coworkflex.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/spaces")
@Tag(name = "Espaces", description = "Gestion des espaces de coworking")
public class SpaceController {

    private final SpaceService spaceService;

    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @GetMapping
    @Operation(summary = "Lister les espaces")
    public ResponseEntity<List<SpaceDTO>> getAll(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer capacity) {
        return ResponseEntity.ok(spaceService.findAll(city, capacity));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un espace")
    public ResponseEntity<SpaceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(spaceService.findById(id));
    }

    @GetMapping("/{id}/desks")
    @Operation(summary = "Postes d'un espace")
    public ResponseEntity<List<DeskDTO>> getDesks(
            @PathVariable Long id,
            @Parameter(description = "Type : OPEN_SPACE, REUNION, PRIVE")
            @RequestParam(required = false) DeskType type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(spaceService.findDesks(id, type, start, end));
    }

    @GetMapping("/cities")
    @Operation(summary = "Liste des villes disponibles")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(spaceService.findAllCities());
    }
}
