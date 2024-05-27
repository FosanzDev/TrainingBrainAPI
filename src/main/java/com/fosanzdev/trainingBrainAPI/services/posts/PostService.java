package com.fosanzdev.trainingBrainAPI.services.posts;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.posts.Post;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IPostService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService implements IPostService {
    @Override
    public boolean createPost(Account account, Post post) {
        return false;
    }

    @Override
    public boolean deletePost(Account account, String postId) {
        return false;
    }

    @Override
    public boolean updatePost(Account account, Post post) {
        return false;
    }

    @Override
    public Post getPost(String postId) {
        return null;
    }

    @Override
    public List<Post> getPostsByAccount(Account account) {
        return List.of();
    }
}
