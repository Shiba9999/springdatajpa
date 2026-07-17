package com.example.SpringDataJpaDemo.service;

import com.example.SpringDataJpaDemo.entities.User;
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

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserDto saveUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setName(createUserDto.getName());
        user.setEmail(createUserDto.getEmail());
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());

    }

    public List<UserDto> getUsers() {

        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : users) {
            UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
            userDtoList.add(userDto);
        }

        return userDtoList;

    }

    public UserDto getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow();
        return new UserDto(user.getId(), user.getName(), user.getEmail());

    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto updateUser(Long id, CreateUserDto updateUserDto) {
        User user = userRepository.findById(id).orElseThrow();

        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());
        //no save method called because


        return new UserDto(user.getId(), user.getName(), user.getEmail());

    }

    @Transactional
    public UserDto patchUser(Long id, CreateUserDto patchUserDto) {

        User user = userRepository.findById(id).orElseThrow();
        if (patchUserDto.getEmail() != null) {
            user.setEmail((patchUserDto.getEmail()));
        }
        if (patchUserDto.getName() != null) {
            user.setName(patchUserDto.getName());
        }

        // no save

        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public List<UserDto> getUserPaginated(int page, int pageSize, String direction, String sortBy) {
        Sort sort;
        sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<User> userPage = userRepository.findAll(pageable);

//        return userPage.getContent().stream()
//                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()))
//                .toList();

        List<UserDto> userDtoList = new ArrayList<>();
        userPage.forEach(user -> {
            userDtoList.add(new UserDto(user.getId(), user.getName(), user.getEmail()));
        });
        return userDtoList;
    }
}
