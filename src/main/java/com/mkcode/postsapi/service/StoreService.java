package com.mkcode.postsapi.service;

import com.mkcode.postsapi.persistence.StoreRepository;
import com.mkcode.postsapi.service.model.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mkcode.postsapi.service.FilterSpecifications.createSpecification;
import static com.mkcode.postsapi.service.PostDtoEntityConverter.convertToPost;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository repository;

    public void save(PostDto postDto) {
        repository.save(convertToPost(postDto));
    }

    public List<PostDto> query(String query) {
        return repository.findAll(createSpecification(query)).stream()
                .map(PostDtoEntityConverter::convertToPostDto)
                .collect(toList());
    }
}
