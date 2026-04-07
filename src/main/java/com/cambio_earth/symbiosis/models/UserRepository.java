package com.cambio_earth.symbiosis.models;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
     @Override
     List<User> findAll();
     Optional<User> findByEmail(String email);
     Optional<User> findByVerificationCode(String verificationCode);
     @Query("SELECT u FROM User u JOIN u.posts p WHERE p.id = :postId")
     Optional<User> findByPostId(@Param("postId") Long postId);
     List<User> findByEnabled(boolean enabled);
}