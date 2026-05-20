package com.inventory.management.user.service;

import com.inventory.management.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User saveUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    Optional<User> login(String username, String password);
    long countUsers();
}