package com.parking.api.service;

import com.parking.api.dto.*;
import com.parking.api.exception.ResourceNotFoundException;
import com.parking.api.model.User;
import com.parking.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        return new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            "Login successful"
        );
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(
            request.getUsername(),
            request.getPassword(),
            request.getRole()
        );

        User savedUser = userRepository.save(user);
        return UserResponse.fromUser(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserResponse::fromUser)
            .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.fromUser(user);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if username is being changed and if it already exists
        if (!user.getUsername().equals(request.getUsername()) &&
            userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setUsername(request.getUsername());
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
