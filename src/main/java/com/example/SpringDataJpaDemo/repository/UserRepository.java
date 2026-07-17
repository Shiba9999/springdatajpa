package com.example.SpringDataJpaDemo.repository;

import com.example.SpringDataJpaDemo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Long> {
}
