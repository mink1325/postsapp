package com.mkcode.postsapi.persistence;

import com.mkcode.postsapi.persistence.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface StoreRepository extends JpaRepository<Post, String>, JpaSpecificationExecutor<Post> {

    List<Post> findAll();

}