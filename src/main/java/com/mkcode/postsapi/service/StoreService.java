package com.mkcode.postsapi.service;

import com.mkcode.postsapi.model.PostDto;
import com.mkcode.postsapi.persistence.StoreRepository;
import com.mkcode.postsapi.persistence.model.Post;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository repository;

    public void save(PostDto postDto) {
        repository.save(concertToPost(postDto));
    }

    public List<PostDto> query(String query) {

        // https://www.baeldung.com/jpa-and-or-criteria-predicates
        // https://github.com/teten-nugraha/spring-data-jpa-dynamic-where/blob/master/src/main/java/id/learn/dynamicwhere/searchSpec/GenericSpesification.java
        // https://medium.com/backend-habit/spring-jpa-make-dynamic-where-using-predicate-and-criteria-84550dfaa182

        Specification<Post> specification = Specification.where(titleContains(query));



        return repository.findAll(createSpecification(query)).stream()
                .map(this::concertToPostDto)
                .collect(toList());
    }

//    public Predicate createPredicate(String expresion) {
//        var position = expresion.indexOf('(');
//        var operatorString = expresion.substring(0, position);
//        Operator operator = Operator.valueOf(operatorString);
//        var something = switch (operator) {
//            case EQUAL ->  createEqualPredicate();
//            case AND -> "";
//            case OR -> "As";
//            case NOT -> "a";
//            case GREATER_THAN -> "a";
//            case LESS_THAN -> "A";
//        };
//    }

    public static Specification<Post> titleContains(String expression) {
        return new Specification<Post>() {
            @Override
            public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

                return builder.like(root.get("title"), expression);
            }
        };
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
        var parameters = operation.getValue();
        return switch (operation.getKey()) {
            case "EQUAL" -> createEqualsSpecification(parameters.get(0), parameters.get(1));
            case "AND" -> createAndSpecification(
                    createSpecification(parameters.get(0)),
                    createSpecification(parameters.get(1)));
            case "OR" -> createOrSpecification(
                    createSpecification(parameters.get(0)),
                    createSpecification(parameters.get(1)));
            case "NOT" -> createNotSpecification(
                    createSpecification(parameters.get(0)));
            case "GREATER_THAN" -> createGreaterThenSpecification(parameters.get(0), parameters.get(1));
            case "LESS_THAN" -> createLessThenSpecification(parameters.get(0), parameters.get(1));
            default -> throw new RuntimeException(format("unknown operator: %s", operation.getKey()));
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
