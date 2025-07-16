package com.rentacar.service;

import com.rentacar.model.User;
import com.rentacar.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.info("Attempting to load user by username: {}", username);
            
            Optional<User> userOptional = userRepository.findByEmail(username);
            
            if (userOptional.isEmpty()) {
                log.warn("User not found with email: {}", username);
                throw new UsernameNotFoundException("User not found with email: " + username);
            }
            
            User user = userOptional.get();
            log.info("User found: {}, enabled: {}", username, user.isEnabled());
            
            if (!user.isEnabled()) {
                log.warn("User account is disabled: {}", username);
                throw new UsernameNotFoundException("User account is disabled");
            }
            
            return user;
        } catch (Exception e) {
            log.error("Error loading user by username: {}", username, e);
            throw e;
        }
    }
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByEmail(username).orElse(null);
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.ROLE_USER);
        user.setEnabled(true);
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public User updateUserProfile(Long id, User user, MultipartFile profileImage, 
                                boolean deleteImage, String newPassword, 
                                String confirmPassword, String currentPassword) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update basic info
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        
        // Update address if available
        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
        }
        
        // Handle password change
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                throw new RuntimeException("Passwords don't match");
            }
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }
        
        // Handle profile image
        handleProfileImage(existingUser, profileImage, deleteImage);
        
        return userRepository.save(existingUser);
    }
    
    @Override
    public User adminUpdateUser(Long id, User user, MultipartFile profileImage, 
                              boolean deleteImage, String newPassword, 
                              String confirmPassword) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
        
        // Update basic info
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setEnabled(user.isEnabled());
        existingUser.setRole(user.getRole());
        
        // Update address
        if (user.getAddress() != null) {
            if (existingUser.getAddress() == null) {
                existingUser.setAddress(user.getAddress());
            } else {
                existingUser.getAddress().setStreet(user.getAddress().getStreet());
                existingUser.getAddress().setCity(user.getAddress().getCity());
                existingUser.getAddress().setPostalCode(user.getAddress().getPostalCode());
                existingUser.getAddress().setState(user.getAddress().getState());
                existingUser.getAddress().setCountry(user.getAddress().getCountry());
            }
        }
        
        // Handle password change
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                throw new RuntimeException("Hasła nie pasują do siebie");
            }
            existingUser.setPassword(passwordEncoder.encode(newPassword));
        }
        
        // Handle profile image
        handleProfileImage(existingUser, profileImage, deleteImage);
        
        return userRepository.save(existingUser);
    }
    
    @Override
    public User findByUsernameWithReservations(String username) {
        return userRepository.findByEmailWithReservations(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Override
    public User findByIdWithReservations(Long id) {
        return userRepository.findByIdWithReservations(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    private void handleProfileImage(User user, MultipartFile profileImage, boolean deleteImage) {
        // Delete existing image if requested
        if (deleteImage && user.getProfileImage() != null) {
            // Code to delete physical file if needed
            user.setProfileImage(null);
        }
        
        // Process new image if provided
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // Generate unique filename
                String filename = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/profiles");
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Save file
                Files.copy(profileImage.getInputStream(), uploadPath.resolve(filename));
                
                // Save filename to user
                user.setProfileImage(filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store profile image", e);
            }
        }
    }
}
