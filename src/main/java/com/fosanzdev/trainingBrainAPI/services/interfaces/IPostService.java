package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.posts.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPostService {

    boolean createPost(Account account, Post post);

    boolean deletePost(Account account, String postId);

    boolean updatePost(Account account, Post post, String postId);

    Post getPost(String postId);

    List<Post> getPostsByAccount(Account account, Pageable pageable);

    List<Post> getRecentPosts(Pageable pageable);
}
