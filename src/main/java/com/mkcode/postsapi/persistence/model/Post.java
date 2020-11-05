package com.mkcode.postsapi.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post {
    @Id
    @NotBlank
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private int views;
    private int timestamp;
}
