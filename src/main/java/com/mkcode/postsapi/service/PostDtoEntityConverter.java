package com.mkcode.postsapi.service;

import com.mkcode.postsapi.persistence.model.Post;
import com.mkcode.postsapi.service.model.PostDto;

class PostDtoEntityConverter {
    static Post convertToPost(PostDto postDto) {
        return new Post(postDto.getId(),
                postDto.getTitle(),
                postDto.getContent(),
                postDto.getViews(),
                postDto.getTimestamp());
    }

    static PostDto convertToPostDto(Post post) {
        return new PostDto(post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViews(),
                post.getTimestamp());
    }
}
