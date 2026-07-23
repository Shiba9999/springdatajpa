package com.example.SpringDataJpaDemo.repository;

import com.example.SpringDataJpaDemo.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for User.
 * Extending JpaRepository gives save/findById/findAll/delete/... for free.
 *
 * Method names like findByEmail are turned into SQL automatically
 * (SELECT ... FROM users WHERE email = ?).
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** Paginated findAll used by UserService.getUserPaginated. */
    @Override
    Page<User> findAll(Pageable pageable);

    /** Used by login, JWT filter, and /api/v1/me APIs. */
    Optional<User> findByEmail(String email);
}
