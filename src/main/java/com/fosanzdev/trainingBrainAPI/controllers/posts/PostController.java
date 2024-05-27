package com.fosanzdev.trainingBrainAPI.controllers.posts;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.posts.Post;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IPostService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post")
@Tag(name = "Post", description = "Controlador de publicaciones")
public class PostController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IPostService postService;


    @Operation(summary = "Nueva publicación", description = "Crea una nueva publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación creada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Post created\"}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Invalid post data\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Internal server error\"}")))
    })
    @PostMapping("/new")
    ResponseEntity<Map<String, Object>> createPost(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la publicación", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "title": "Título de la publicación",
                                        "content": "Contenido de la publicación"
                                    }
                                    """)))
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            Post post = Post.fromMap(body);
            if (post == null) return ResponseEntity.badRequest().body(Map.of("error", "Invalid post data"));

            if (postService.createPost(account, post)) {
                return ResponseEntity.ok(Map.of("message", "Post created"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Post request malformed"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }

    @Operation(summary = "Actualiza una publicación", description = "Actualiza una publicación existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación actualizada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Post updated\"}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Invalid post data\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}"))),
            @ApiResponse(responseCode = "404", description = "Publicación no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Post does not exist or is not yours\"}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Internal server error\"}")))
    })
    @PostMapping("/update/{postId}")
    ResponseEntity<Map<String, Object>> updatePost(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "Identificador de la publicación", required = true)
            @PathVariable String postId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la publicación", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "title": "Título de la publicación",
                                        "content": "Contenido de la publicación"
                                    }
                                    """)))
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            Post post = Post.fromMap(body);
            if (post == null) return ResponseEntity.badRequest().body(Map.of("error", "Invalid post data"));

            if (postService.updatePost(account, post, postId)) {
                return ResponseEntity.ok(Map.of("message", "Post updated"));
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "Post does not exist or is not yours"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }

    @Operation(summary = "Obtiene una publicación", description = "Obtiene una publicación por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación obtenida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "id": "id de la publicación",
                                        "title": "Título de la publicación",
                                        "content": "Contenido de la publicación",
                                        "creationDateTime": "Fecha de creación de la publicación",
                                        "author": {
                                            "id": "id de la cuenta",
                                            "username": "Nombre de usuario",
                                            "name": "Nombre",
                                            "isProfessional": "true si es profesional, false si no",
                                            "relatedId": "id de la cuenta user o professional asociada"
                                        }
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Publicación no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Post not found\"}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Internal server error\"}")))
    })
    @GetMapping("/{postId}")
    ResponseEntity<Map<String, Object>> getPost(
            @Parameter(description = "Identificador de la publicación", required = true)
            @PathVariable String postId
    ) {
        try {
            Post post = postService.getPost(postId);
            if (post == null) return ResponseEntity.status(404).body(Map.of("error", "Post not found"));
            return ResponseEntity.ok(post.toMap());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }

    @Operation(summary = "Obtiene las publicaciones de una cuenta", description = "Obtiene las publicaciones de una cuenta por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicaciones obtenidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "posts": [
                                            {
                                                "id": "id de la publicación",
                                                "title": "Título de la publicación",
                                                "content": "Contenido de la publicación",
                                                "creationDateTime": "Fecha de creación de la publicación",
                                                "author": {
                                                    "id": "id de la cuenta",
                                                    "username": "Nombre de usuario",
                                                    "name": "Nombre",
                                                    "isProfessional": "true si es profesional, false si no",
                                                    "relatedId": "id de la cuenta user o professional asociada"
                                                }
                                            }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/from/{accountId}")
    ResponseEntity<Map<String, Object>> getPostsByAccount(
            @Parameter(description = "Identificador de la cuenta", required = true)
            @PathVariable String accountId,

            @Parameter(description = "Número de página")
            @RequestParam(required = false) Integer page,

            @Parameter(description = "Número de elementos por página. Máximo de 20")
            @RequestParam(required = false) Integer size
    ) {
        try {
            if (size == null || size <= 0 || size > 20) size = 20;
            if (page == null || page < 0) page = 0;

            Pageable pageable = PageRequest.ofSize(size).withPage(page);
            Account account = accountService.getAccountById(accountId);
            if (account == null) return ResponseEntity.status(404).body(Map.of("error", "Account not found"));

            return ResponseEntity.ok(Map.of("posts", postService.getPostsByAccount(account, pageable).stream().map(Post::toMap)));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }

    @Operation(summary = "Obtiene las publicaciones recientes", description = "Obtiene las publicaciones más recientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicaciones obtenidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "posts": [
                                            {
                                                "id": "id de la publicación",
                                                "title": "Título de la publicación",
                                                "content": "Contenido de la publicación",
                                                "creationDateTime": "Fecha de creación de la publicación",
                                                "author": {
                                                    "id": "id de la cuenta",
                                                    "username": "Nombre de usuario",
                                                    "name": "Nombre",
                                                    "isProfessional": "true si es profesional, false si no",
                                                    "relatedId": "id de la cuenta user o professional asociada"
                                                }
                                            }
                                        ]
                                    }
                                    """)))
    })
    @GetMapping("/recent")
    ResponseEntity<Map<String, Object>> getRecentPosts(
            @Parameter(description = "Número de página")
            @RequestParam(required = false) Integer page,

            @Parameter(description = "Número de elementos por página. Máximo de 20")
            @RequestParam(required = false) Integer size
    ) {
        try {
            if (size == null || size <= 0 || size > 20) size = 20;
            if (page == null || page < 0) page = 0;

            Pageable pageable = PageRequest.ofSize(size).withPage(page);
            return ResponseEntity.ok(Map.of("posts", postService.getRecentPosts(pageable).stream().map(Post::toMap)));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/delete/{postId}")
    ResponseEntity<Map<String, Object>> deletePost(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "Identificador de la publicación", required = true)
            @PathVariable String postId
    ) {
        try {
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            if (postService.deletePost(account, postId)) {
                return ResponseEntity.ok(Map.of("message", "Post deleted"));
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "Post does not exist or is not yours"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
        }
    }
}
