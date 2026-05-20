package com.inventory.management.user.service;

import com.inventory.management.user.model.User;
import com.inventory.management.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new IllegalArgumentException("Username already exists.");
        if (userRepository.existsByEmail(user.getEmail()))
            throw new IllegalArgumentException("Email already exists.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updated) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (userRepository.existsByUsernameAndIdNot(updated.getUsername(), id))
            throw new IllegalArgumentException("Username already taken.");
        if (userRepository.existsByEmailAndIdNot(updated.getEmail(), id))
            throw new IllegalArgumentException("Email already taken.");
        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        existing.setRole(updated.getRole());
        existing.setActive(updated.getActive());
        if (updated.getPassword() != null && !updated.getPassword().isBlank())
            existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        u.setActive(false);
        userRepository.save(u);
    }

    @Override
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }
}