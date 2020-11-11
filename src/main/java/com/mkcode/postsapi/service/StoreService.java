package com.mkcode.postsapi.service;

import com.mkcode.postsapi.persistence.StoreRepository;
import com.mkcode.postsapi.persistence.model.Post;
import com.mkcode.postsapi.service.model.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository repository;

    public void save(PostDto postDto) {
        repository.save(concertToPost(postDto));
    }

    public List<PostDto> query(String query) {
        return repository.findAll(createSpecification(query)).stream()
                .map(this::concertToPostDto)
                .collect(toList());
    }

    private Post concertToPost(PostDto postDto) {
        return new Post(postDto.getId(),
                postDto.getTitle(),
                postDto.getContent(),
                postDto.getViews(),
                postDto.getTimestamp());
    }

    private PostDto concertToPostDto(Post post) {
        return new PostDto(post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViews(),
                post.getTimestamp());
    }

    public static Specification<Post> createSpecification(String expression) {
        var operation = OperationParser.parseOperation(expression);
        var parameters = operation.getParameters();
        return switch (operation.getOperator()) {
            case EQUAL -> createEqualsSpecification(parameters.get(0), parameters.get(1));
            case AND -> createAndSpecification(
                    createSpecification(parameters.get(0)),
                    createSpecification(parameters.get(1)));
            case OR -> createOrSpecification(
                    createSpecification(parameters.get(0)),
                    createSpecification(parameters.get(1)));
            case NOT -> createNotSpecification(
                    createSpecification(parameters.get(0)));
            case GREATER_THAN -> createGreaterThenSpecification(parameters.get(0), parameters.get(1));
            case LESS_THAN -> createLessThenSpecification(parameters.get(0), parameters.get(1));
        };
    }

    private static Specification<Post> createLessThenSpecification(String fieldName, String value) {
        //todo validate value is a number and field is number type
        return (root, query, builder) -> builder.lessThan(root.get(fieldName), value);
    }

    private static Specification<Post> createGreaterThenSpecification(String fieldName, String value) {
        //todo validate value is a number and field is number type
        return (root, query, builder) -> builder.greaterThan(root.get(fieldName), value);
    }

    private static Specification<Post> createAndSpecification(Specification<Post> specification, Specification<Post> specification1) {
        return specification.and(specification1);
    }
    private static Specification<Post> createOrSpecification(Specification<Post> specification, Specification<Post> specification1) {
        return specification.or(specification1);
    }

    private static Specification<Post> createNotSpecification(Specification<Post> specification) {
        return (root, query, builder) -> specification.toPredicate(root, query, builder).not();
    }

    private static Specification<Post> createEqualsSpecification(String fieldName, String value) {
        return (root, query, builder) -> builder.equal(root.get(fieldName), value);
    }
}
