package com.fosanzdev.trainingBrainAPI.controllers.grpc.posts;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.posts.Post;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IPostService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
import com.fosanzdev.trainingBrainGrpcInterface.auth.BasicAccountInfo;
import com.fosanzdev.trainingBrainGrpcInterface.posts.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@GrpcService
public class grpc_PostController extends PostsServiceGrpc.PostsServiceImplBase {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IPostService postService;

    @Override
    public void addPost(AddPostRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
            }

            Post post = new Post();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());

            if (postService.createPost(account, post)) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Error adding post");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error adding post");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void updatePost(UpdatePostRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
            }

            Post post = new Post();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());

            if (postService.updatePost(account, post, request.getId())) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Error updating post");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error updating post");
            response.onError(status.asRuntimeException());
        }
    }

    public PostResponse buildPostResponse(Post post) {

        //Repeated code, but anyway, used to avoid autowiring
        BasicAccountInfo accountInfo = BasicAccountInfo.newBuilder()
                .setId(post.getAuthor().getId())
                .setUsername(post.getAuthor().getUsername() != null ? post.getAuthor().getUsername() : "")
                .setName(post.getAuthor().getName() != null ? post.getAuthor().getName() : "")
                .setIsProfessional(post.getAuthor().isProfessional())
                .setRelatedId(post.getAuthor().isProfessional() ? post.getAuthor().getProfessionalDetails().getId() : post.getAuthor().getUserDetails().getId())
                .build();

        return PostResponse.newBuilder()
                .setId(post.getId())
                .setTitle(post.getTitle() != null ? post.getTitle() : "")
                .setContent(post.getContent() != null ? post.getContent() : "")
                .setCreationDateTime(post.getCreationDateTime().toString())
                .setAuthor(accountInfo)
                .build();
    }

    @Override
    public void getPost(GetPostRequest request, StreamObserver<PostResponse> response) {
        try {
            Post post = postService.getPost(request.getId());
            if (post == null) {
                Status status = Status.NOT_FOUND.withDescription("Post not found");
                response.onError(status.asRuntimeException());
                return;
            }

            response.onNext(buildPostResponse(post));
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting post");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getPostsFromAccount(AccountId request, StreamObserver<PostListResponse> response) {
        try {
            int size = request.getPagination().getPageSize();
            int page = request.getPagination().getPage();

            if (size <= 0 || size > 20) size = 20;
            if (page < 0) page = 0;

            Pageable pageable = PageRequest.ofSize(size).withPage(page);
            Account account = accountService.getAccountById(request.getId());
            if (account == null) {
                Status status = Status.NOT_FOUND.withDescription("Account not found");
                response.onError(status.asRuntimeException());
                return;
            }

            PostListResponse.Builder postListResponseBuilder = PostListResponse.newBuilder();
            for (Post post : postService.getPostsByAccount(account, pageable)) {
                postListResponseBuilder.addPosts(buildPostResponse(post));
            }
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting posts");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getRecentPosts(Pagination pagination, StreamObserver<PostListResponse> response) {
        try {
            int size = pagination.getPageSize();
            int page = pagination.getPage();

            if (size <= 0 || size > 20) size = 20;
            if (page < 0) page = 0;

            Pageable pageable = PageRequest.ofSize(size).withPage(page);

            PostListResponse.Builder postListResponseBuilder = PostListResponse.newBuilder();
            for (Post post : postService.getRecentPosts(pageable)) {
                postListResponseBuilder.addPosts(buildPostResponse(post));
            }

            response.onNext(postListResponseBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting posts");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void deletePost(GetPostRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
            }

            if (postService.deletePost(account, request.getId())) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Error deleting post");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error deleting post");
            response.onError(status.asRuntimeException());
        }
    }


}
