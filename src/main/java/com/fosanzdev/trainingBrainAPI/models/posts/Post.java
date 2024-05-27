package com.fosanzdev.trainingBrainAPI.models.posts;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100)
    private String title;

    @Column(length = 1000)
    private String content;

    private Instant creationDateTime;

    @ManyToOne
    @JoinColumn(name = "fk_author", referencedColumnName = "id")
    private Account author;

    public Post fromMap(Map<String, Object> jsonMap){
        try{
            Post post = new Post();
            post.setTitle((String) jsonMap.get("title"));
            post.setContent((String) jsonMap.get("content"));
            post.setCreationDateTime(Instant.now());
            return post;
        } catch (Exception e){
            return null;
        }
    }

    public Map<String, Object> toMap(){
        return Map.of(
                "id", id,
                "title", title,
                "content", content,
                "creationDateTime", creationDateTime,
                "author", author.toBasicMap()
        );
    }
}
