package com.example.SpringDataJpaDemo.service;

import com.example.SpringDataJpaDemo.entities.User;
import com.example.SpringDataJpaDemo.exception.UserNotFoundException;
import com.example.SpringDataJpaDemo.repository.UserRepository;
import com.example.SpringDataJpaDemo.dto.CreateUserDto;
import com.example.SpringDataJpaDemo.dto.UserDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic for User CRUD / queries.
 * Controllers call this; this class talks to UserRepository (DB).
 *
 * @Service — Spring service bean
 * @AllArgsConstructor — injects UserRepository
 */
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /** Legacy create (password not hashed here). Prefer AuthService.registerUser. */
    public UserDto saveUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setName(createUserDto.getName());
        user.setEmail(createUserDto.getEmail());
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    /** Returns all users as DTOs (no password leaked). */
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users) {
            userDtoList.add(new UserDto(user.getId(), user.getName(), user.getEmail()));
        }
        return userDtoList;
    }

    /** Find by primary key or throw UserNotFoundException (mapped to 404). */
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    /**
     * Find by email — used when identity comes from JWT subject (login email).
     */
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email " + email));
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Full update of name/email.
     * @Transactional — changes on the managed entity are flushed to DB at commit
     * (no explicit save needed while the entity is managed).
     */
    @Transactional
    public UserDto updateUser(Long id, CreateUserDto updateUserDto) {
        User user = userRepository.findById(id).orElseThrow();
        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    /** Partial update: only apply fields that the client sent (non-null). */
    @Transactional
    public UserDto patchUser(Long id, CreateUserDto patchUserDto) {
        User user = userRepository.findById(id).orElseThrow();
        if (patchUserDto.getEmail() != null) {
            user.setEmail(patchUserDto.getEmail());
        }
        if (patchUserDto.getName() != null) {
            user.setName(patchUserDto.getName());
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    /**
     * Paginated + sorted list.
     * PageRequest.of(page, size, sort) — Spring Data pagination object
     */
    public List<UserDto> getUserPaginated(int page, int pageSize, String direction, String sortBy) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDto> userDtoList = new ArrayList<>();
        userPage.forEach(user ->
                userDtoList.add(new UserDto(user.getId(), user.getName(), user.getEmail())));
        return userDtoList;
    }
}
