package com.mkcode.postsapi.service;

import com.mkcode.postsapi.model.PostDto;
import com.mkcode.postsapi.persistence.StoreRepository;
import com.mkcode.postsapi.persistence.model.Operator;
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

    public static void main(String[] args) {
        String mydata = "AND(EQUAL(id, \"Asdf\"),EQUAL(title, \"some\"))";
        Specification<Post> specification = createSpecification(mydata);
        System.out.println(specification);

    }

    public static Specification<Post> createSpecification(String expression) {
        Pair<String, Pair<String, String>> evaluate = evaluate(expression);
        return switch (evaluate.getKey()) {
            case "EQUAL" -> createEqualsSpecification(evaluate.getValue().getKey(), evaluate.getValue().getValue());
            case "AND" -> createAndSpecification(
                    createSpecification(evaluate.getValue().getKey()),
                    createSpecification(evaluate.getValue().getValue()));
            case "NOT" -> createNotSpecification(
                    createSpecification(evaluate.getValue().getKey()));
            default -> throw new RuntimeException("unknown operator");
        };
    }

    private static Specification<Post> createAndSpecification(Specification<Post> specification, Specification<Post> specification1) {
        return specification.and(specification1);
    }
    private static Specification<Post> createNotSpecification(Specification<Post> specification) {
        return (root, query, builder) -> specification.toPredicate(root, query, builder).not();
    }

    private static Specification<Post> createEqualsSpecification(String fieldName, String value) {
        return (root, query, builder) -> builder.equal(root.get(fieldName), value);
    }

    private static Pair<String, Pair<String, String>> evaluate(String expression) {
        Pattern pattern = Pattern.compile("(NOT|EQUAL|AND)\\((.*)\\)");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find()) {
            System.out.println(matcher.group(0)); // jei nera exception validus
            System.out.println(matcher.group(1)); // operatorius
            System.out.println(matcher.group(2)); // operatoriaus parametrai
            var operandsStr = matcher.group(2);
            int openBrackets = 0;
            int splitter = 0;
            for (int i = 0; i < operandsStr.length(); i++){
                if (operandsStr.charAt(i) == '(') openBrackets++;
                if (operandsStr.charAt(i) == ')') openBrackets--;
                if (operandsStr.charAt(i) == ',' && openBrackets == 0) splitter = i;
            }
            System.out.println("-----");
            System.out.println(operandsStr.substring(0, splitter));
            System.out.println("-----");
            System.out.println(operandsStr.substring(splitter+1));

            Pair<String, String> operands;
            if (splitter != 0) {
                operands = Pair.of(operandsStr.substring(0, splitter), operandsStr.substring(splitter+1));
            } else
                operands = Pair.of(operandsStr, null);

            return Pair.of(matcher.group(1), operands);
        } else {
            throw new RuntimeException("expression is not valid");
        }
    }
}
