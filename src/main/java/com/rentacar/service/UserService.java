package com.rentacar.service;

import com.rentacar.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    
    User findByUsername(String username);
    
    User findById(Long id);
    
    List<User> findAll();
    
    Optional<User> getUserByEmail(String email);
    
    User getUserById(Long id);
    
    @Transactional(readOnly = true)
    List<User> getAllUsers();
    
    User registerUser(User user);
    
    User updateUser(User user);
    
    void deleteUser(Long id);
    
    User updateUserProfile(Long id, User user, MultipartFile profileImage, 
                          boolean deleteImage, String newPassword, 
                          String confirmPassword, String currentPassword);
    
    User adminUpdateUser(Long id, User user, MultipartFile profileImage, 
                        boolean deleteImage, String newPassword, 
                        String confirmPassword);
                        
    @Transactional(readOnly = true)
    User findByUsernameWithReservations(String username);
    
    @Transactional(readOnly = true)
    User findByIdWithReservations(Long id);
}
