package com.fosanzdev.trainingBrainAPI.repositories.posts;

import com.fosanzdev.trainingBrainAPI.models.posts.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, String> {
}
