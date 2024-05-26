package com.fosanzdev.trainingBrainAPI.controllers.search;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.repositories.data.ProfessionalRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.ProfessionalSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
@Tag(name = "Search", description = "Controlador de búsqueda")
public class SearchController {

    private static final List<String> AVAILABLE_FILTERS = List.of("branch", "worktitle");

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Operation(summary = "Busca profesionales por filtro y subfiltro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profesionales encontrados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "professionals": [
                                            {
                                                "id": "364f2933-c91e-4641-...",
                                                "name": "Nombre",
                                                "surname": "Apellidos",
                                                "rating": 5,
                                                "workTitle": {
                                                    "id": "364f2933-c91e-4641-...",
                                                    "name": "Título",
                                                    "branch": {
                                                        "id": "364f2933-c91e-4641-...",
                                                        "name": "Rama"
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Petición inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Invalid filter\"}")))
    })
    @GetMapping("/professionals/{filter}/{filterId}")
    public ResponseEntity<Map<String, Object>> searchProfessionals(
            @Parameter(description = "Filtro de búsqueda", required = true, example = "branch/worktitle")
            @PathVariable String filter,

            @Parameter(description = "ID del filtro", required = true, example = "364f2933-c91e-4641-...")
            @PathVariable Long filterId,

            @Parameter(description = "Subfiltro de búsqueda. Todas son opcionales",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "name": "Nombre",
                                        "hasSkills": [1, 2, 3],
                                        "rating": 5
                                    }
                                    """)))
            @RequestBody(required = false) Map<String, Object> body,

            @Parameter(description = "Page number", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        if (!AVAILABLE_FILTERS.contains(filter)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid filter"));
        }

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        String name = null;
        List<Long> hasSkills = null;
        Integer rating = null;

        if (body != null && !body.isEmpty()) {
            name = body.containsKey("name") ? (String) body.get("name") : null;
            hasSkills = body.containsKey("hasSkills") ? (List<Long>) body.get("hasSkills") : null;
            rating = body.containsKey("rating") ? (Integer) body.get("rating") : null;
        }

        Page<Professional> professionals = professionalRepository.findAll(
                new ProfessionalSpecification(filter, filterId, name, hasSkills, rating), pageable
        );

        return ResponseEntity.ok(Map.of(
                "professionals", professionals.stream().map(Professional::toMap).collect(Collectors.toList())
        ));
    }

    @Operation(summary = "Lista de profesionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de profesionales",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "professionals": [
                                            {
                                                "id": "364f2933-c91e-4641-...",
                                                "name": "Nombre",
                                                "surname": "Apellidos",
                                                "rating": 5,
                                                "workTitle": {
                                                    "id": "364f2933-c91e-4641-...",
                                                    "name": "Título",
                                                    "branch": {
                                                        "id": "364f2933-c91e-4641-...",
                                                        "name": "Rama"
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/professionals")
    public ResponseEntity<Map<String, Object>> listProfessionals(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Professional> professionals = professionalRepository.findAll(pageable);

        return ResponseEntity.ok(Map.of(
                "professionals", professionals.stream().map(Professional::toMap).collect(Collectors.toList())
        ));
    }
}
