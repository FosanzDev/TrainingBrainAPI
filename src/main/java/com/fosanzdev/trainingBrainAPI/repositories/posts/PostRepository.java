package com.fosanzdev.trainingBrainAPI.repositories.posts;

import com.fosanzdev.trainingBrainAPI.models.posts.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {

    @Query("SELECT p FROM Post p WHERE p.author.id = :accountId ORDER BY p.creationDateTime DESC")
    List<Post> findAllByAuthor(String accountId, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.creationDateTime DESC")
    List<Post> findAllRecent(Pageable pageable);
}
