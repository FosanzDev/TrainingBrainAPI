package com.fosanzdev.trainingBrainAPI.controllers.posts;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.posts.Post;
import com.fosanzdev.trainingBrainAPI.services.auth.AccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IPostService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.posts.PostService;
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

import java.util.Map;

@RestController
@RequestMapping("/post")
@Tag(name = "Post", description = "Controlador de publicaciones")
public class PostController {

    private final IAccountService accountService;

    private final IPostService postService;

    public PostController(IAccountService accountService, IPostService postService) {
        this.accountService = accountService;
        this.postService = postService;
    }

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
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
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
    ){
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

    @PostMapping("/update/{postId}")
    ResponseEntity<Map<String, Object>> updatePost(){
        return ResponseEntity.ok(Map.of("message", "Post updated"));
    }

    @GetMapping("/{postId}")
    ResponseEntity<Map<String, Object>> getPost(){
        return ResponseEntity.ok(Map.of("message", "Post retrieved"));
    }

    @GetMapping("/from/{accountId}")
    ResponseEntity<Map<String, Object>> getPostsByAccount(){
        return ResponseEntity.ok(Map.of("message", "Posts retrieved"));
    }

    @DeleteMapping("/delete/{postId}")
    ResponseEntity<Map<String, Object>> deletePost(){
        return ResponseEntity.ok(Map.of("message", "Post deleted"));
    }



}
