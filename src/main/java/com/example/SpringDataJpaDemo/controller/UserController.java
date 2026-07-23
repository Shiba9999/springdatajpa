package com.example.SpringDataJpaDemo.controller;

import com.example.SpringDataJpaDemo.service.UserService;
import com.example.SpringDataJpaDemo.dto.CreateUserDto;
import com.example.SpringDataJpaDemo.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-style user management APIs (paths still use /{id}).
 * Most of these require ROLE_ADMIN — see SecurityConfig.
 *
 * Prefer /api/v1/me for the logged-in user's own profile (token-based, no id in URL).
 *
 * @RestController — JSON REST controller
 * @RequestMapping("/api/v1/users") — base path
 * @AllArgsConstructor — Lombok constructor injection of UserService
 */
@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // Register moved to AuthController: POST /api/v1/auth/register

    /** GET /api/v1/users — list all users (admin). */
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }

    /**
     * GET /api/v1/users/paginated?page=&pageSize=&direction=&sortBy=
     * @RequestParam — query string parameters
     * defaultValue — used when the client omits that query param
     */
    @GetMapping("/paginated")
    public ResponseEntity<List<UserDto>> getUserPaginated(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "name") String sortBy) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserPaginated(page, pageSize, direction, sortBy));
    }

    /**
     * GET /api/v1/users/{id}
     * @PathVariable — binds {id} from the URL path
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    /** DELETE /api/v1/users/{id} — 204 No Content on success. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /** PATCH /api/v1/users/{id} — partial update (only non-null fields). */
    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> patchUser(@PathVariable Long id, @RequestBody CreateUserDto patchUserDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.patchUser(id, patchUserDto));
    }

    /** PUT /api/v1/users/{id} — full update of name/email. */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody CreateUserDto updateUserDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, updateUserDto));
    }

}
