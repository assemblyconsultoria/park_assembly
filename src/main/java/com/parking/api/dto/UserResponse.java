package com.parking.api.dto;

import com.parking.api.model.User;
import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String username;
    private String role;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String username, String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
