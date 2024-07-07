package com.example.spring.repository;

import com.example.spring.model.User;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<@Valid User, Long> {
    List<@Valid User> findAll();

    Optional<@Valid User> findById(Long id);

    Optional<@Valid User> findByEmail(String email);

    @Query("select u from User u where u.userName like %:pattern% or u.email like %:pattern%")
    List<@Valid User> findByPartOfUsernameOrEmail(String pattern);

    void deleteById(Long id);

    void deleteByEmail(String email);

    void delete(@Valid User user);

    User save(@Valid User user);
}