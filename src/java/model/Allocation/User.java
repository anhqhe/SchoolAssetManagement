package model.Allocation;

import java.time.LocalDateTime;
import java.util.List;

public class User {
    private long userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private List<String> roles; // store role codes like "ADMIN", "TEACHER"

    public User() {
    }

    public User(long userId, String username, String passwordHash, String fullName, String email, String phone, boolean isActive, LocalDateTime createdAt, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.roles = roles;
    }
    
    public User(long userId, String username, String passwordHash, boolean isActive, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
        this.roles = roles;
    }
    
    
    
    // getters 
    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getRoles() {
        return roles;
    }
    
    //setters

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
}
