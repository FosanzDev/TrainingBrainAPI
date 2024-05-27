package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.posts.Post;

import java.util.List;

public interface IPostService {

    boolean createPost(Account account, Post post);

    boolean deletePost(Account account, String postId);

    boolean updatePost(Account account, Post post);

    Post getPost(String postId);

    List<Post> getPostsByAccount(Account account);
}
