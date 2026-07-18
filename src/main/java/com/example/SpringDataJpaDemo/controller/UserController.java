package com.example.SpringDataJpaDemo.controller;

import com.example.SpringDataJpaDemo.service.UserService;
import com.example.SpringDataJpaDemo.dto.CreateUserDto;
import com.example.SpringDataJpaDemo.dto.UserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
//this is for no need to write contructor for UserService file here
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }


public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser( @Valid @RequestBody CreateUserDto createUserDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(createUserDto));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<UserDto>> getUserPaginated(@RequestParam int page ,@RequestParam int pageSize,
                                                          @RequestParam (defaultValue = "asc") String direction,
                                                          @RequestParam (defaultValue = "name") String sortBy

                                                          ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserPaginated(page,pageSize,direction,sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> patchUser(@PathVariable Long id, @RequestBody CreateUserDto patchUserDto) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.patchUser(id, patchUserDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody CreateUserDto updateUserDto) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, updateUserDto));
    }

}
