package com.mkcode.postsapi.rest;

import com.mkcode.postsapi.service.StoreService;
import com.mkcode.postsapi.service.model.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @ResponseStatus(OK)
    @PostMapping(consumes = "application/json")
    public void save(@Valid @RequestBody PostDto postDto) {
        log.debug("Post request with postId: {}", postDto.getId());
        storeService.save(postDto);
    }

    @ResponseStatus(OK)
    @GetMapping(produces = "application/json")
    public List<PostDto> query(@RequestParam String query) {
        log.debug("Get request with query: {}", query);
        return storeService.query(query);
    }
}

