package com.mkcode.postsapi.rest;

import com.mkcode.postsapi.model.PostDto;
import com.mkcode.postsapi.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @ResponseStatus(OK)
    @PostMapping(consumes = "application/json")
    public void save(@RequestBody PostDto contractDto) {
        storeService.save(contractDto);
    }

    @ResponseStatus(OK)
    @GetMapping(produces = "application/json")
    public List<PostDto> query(@RequestParam String query) {
        return storeService.query(query);
    }
}

