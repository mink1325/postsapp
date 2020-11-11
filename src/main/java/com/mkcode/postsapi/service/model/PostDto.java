package com.mkcode.postsapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
public class PostDto {
    @NotBlank
    private final String id;
    @NotBlank
    private final String title;
    @NotBlank
    private final String content;
    private final int views;
    private final int timestamp;
}
