package com.fosanzdev.trainingBrainAPI.services.posts;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.posts.Post;
import com.fosanzdev.trainingBrainAPI.repositories.posts.PostRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IPostService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService implements IPostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    @Override
    public boolean createPost(Account account, Post post) {
        try{
            post.setAuthor(account);
            postRepository.save(post);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deletePost(Account account, String postId) {
        Post postToDelete = postRepository.findById(postId).orElse(null);
        if(postToDelete == null) return false;

        if(!postToDelete.getAuthor().getId().equals(account.getId())) return false;

        postRepository.delete(postToDelete);
        return true;
    }

    @Override
    public boolean updatePost(Account account, Post post, String postId) {
        Post postToUpdate = postRepository.findById(postId).orElse(null);
        if(postToUpdate == null) return false;

        if(!postToUpdate.getAuthor().getId().equals(account.getId())) return false;

        postToUpdate.setTitle(post.getTitle());
        postToUpdate.setContent(post.getContent());
        postToUpdate.setCreationDateTime(post.getCreationDateTime());

        postRepository.save(postToUpdate);
        return true;
    }

    @Override
    public Post getPost(String postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public List<Post> getPostsByAccount(Account account, Pageable pageable) {
        return postRepository.findAllByAuthor(account.getId(), pageable);
    }

    @Override
    public List<Post> getRecentPosts(Pageable pageable) {
        return postRepository.findAllRecent(pageable);
    }
}
