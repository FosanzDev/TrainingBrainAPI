package com.fosanzdev.trainingBrainAPI.controllers.mood;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import com.fosanzdev.trainingBrainAPI.models.mood.Mood;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IMoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mood")
@Tag(name = "Mood", description = "Controlador de estados de ánimo")
public class MoodController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IMoodService moodService;

    @Operation(summary = "Añade un estado de ánimo al historial del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de ánimo añadido correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Entry added\"}"))),
            @ApiResponse(responseCode = "400", description = "Petición inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Invalid request, check moodId\"}")))
    })
    @PostMapping("/add")
    ResponseEntity<Map<String, String>> addEntry(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ID del estado de ánimo", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"moodId\":\"<id>\"}")))
            @RequestBody Map<String, String> body
    ) {
        String token = bearer.split(" ")[1]; // Bearer token
        String moodId = body.get("moodId");
        Account account = accountService.getAccountByAccessToken(token);

        if (account == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
        }

        if (account.isProfessional()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Professionals can't add moods"));
        }

        try{
            moodService.addEntry(account.getId(), moodId);
            return ResponseEntity.ok(Map.of("message", "Entry added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid request, check moodId"));
        }
    }

    @Operation(summary = "Obtiene el historial de estados de ánimo del usuario", description = """
            Obtiene el historial de estados de ánimo del usuario, con la posibilidad de limitar el número de resultados y de paginarlos.
            Para paginar, se pueden usar los parámetros limit (limite de resultados) y offset (desplazamiento de resultados).
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de estados de ánimo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"history\":[{\"mood\":\"<mood>\",\"date\":\"2024-05-15T16:08:48.255+00:00\"}]}"))),
            @ApiResponse(responseCode = "400", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Invalid token\"}")))
    })
    @GetMapping("/history")
    ResponseEntity<Map<String, Object>> getHistory(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "Número de resultados a obtener", required = false, example = "10")
            @RequestParam(required = false) Integer limit,

            @Parameter(description = "Desplazamiento de resultados", required = false, example = "0")
            @RequestParam(required = false) Integer offset
    ) {
        String token = bearer.split(" ")[1]; // Bearer token
        Account account = accountService.getAccountByAccessToken(token);

        if (account == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
        }

        if (account.isProfessional()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Professionals can't see self moods history"));
        }

        List<AccountMood> accountMoods = moodService.getHistory(account.getId(), limit, offset);

        return ResponseEntity.ok(Map.of("history", accountMoods));
    }


    @Operation(summary = "Obtiene el historial de estados de ánimo de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de estados de ánimo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"history\":[{\"mood\":\"<mood>\",\"date\":\"2024-05-15T16:08:48.255+00:00\"}]}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Invalid token\"}"))),
            @ApiResponse(responseCode = "404", description = "ID de usuario inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(example = "{\"message\":\"Invalid user id\"}"))),
            @ApiResponse(responseCode = "403", description = "Usuarios no pueden ver el historial de estados de ánimo de otros usuarios",
                    content = @Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(example = "{\"message\":\"Users can't see other users moods history\"}")))
    })
    @GetMapping("/history/{id}")
    ResponseEntity<Map<String, Object>> getHistoryById(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "ID del usuario", required = true, example = "0b7173da-9e99-4347-8c4f-1663d82ef511")
            @PathVariable String id,

            @Parameter(description = "Número de resultados a obtener", required = false, example = "10")
            @RequestParam(required = false) Integer limit,

            @Parameter(description = "Desplazamiento de resultados", required = false, example = "0")
            @RequestParam(required = false) Integer offset
    ) {
        String token = bearer.split(" ")[1]; // Bearer token
        Account account = accountService.getAccountByAccessToken(token);

        if (account == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
        }

        if (!account.isProfessional()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Users can't see other users moods history"));
        }

        List<AccountMood> accountMoods;
        Account user = accountService.getAccountById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid user id"));
        }

        if (user.isProfessional()){
            return ResponseEntity.badRequest().body(Map.of("message", "Professionals don't have moods history"));
        }


        accountMoods = moodService.getHistory(id, limit, offset);
        return ResponseEntity.ok(Map.of("history", accountMoods));
    }


    @Operation(summary = "Obtiene todos los estados de ánimo disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estados de ánimo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"moods\":[{\"id\":\"0b7173da-9e99-4347-8c4f-1663d82ef511\",\"name\":\"Energetic\", \"description\":\"You feel energetic, awake or lively\", \"color\":\"YELLOW\", \"icon\":\"/static/img/moods/energetic.png\"}, {\"...\": \"...\"}]}"))),
    })
    @GetMapping("/all")
    ResponseEntity<Map<String, List<Mood>>> getMoods() {
        List<Mood> accountMoods = moodService.getMoods();
        return ResponseEntity.ok(Map.of("moods", accountMoods));
    }

}
