package com.log75.blog.repository;

import com.log75.blog.model.PersistentLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

/**
 * Created by alireza on 5/14/20.
 */
public interface PersistentRepository extends JpaRepository<PersistentLogin, String> {

    @Modifying
    void deleteByUsername(String userName);

    @Modifying
    void deleteBySeries(String series);

}

