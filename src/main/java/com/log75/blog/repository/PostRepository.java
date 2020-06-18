package com.log75.blog.repository;

import com.log75.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by alireza on 6/17/20.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

}
