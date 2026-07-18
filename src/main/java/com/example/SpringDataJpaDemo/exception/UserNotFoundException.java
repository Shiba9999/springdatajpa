package com.example.SpringDataJpaDemo.exception;

public class UserNotFoundException extends RuntimeException {

    //custom exception extends and used super to call the parent class constructor
    //then in service class of userService we throw like this  User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    //then we made the global handler


    public UserNotFoundException (String errorMsg){
        super(errorMsg);
    }
}
