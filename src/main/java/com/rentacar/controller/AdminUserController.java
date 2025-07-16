package com.rentacar.controller;

import com.rentacar.model.User;
import com.rentacar.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/user-management")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("Invalid user Id: " + id);
        }
        model.addAttribute("user", user);
        return "admin/users/edit";
    }

    @PostMapping("/update")
    public String updateUser(
            @RequestParam("id") Long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "enabled", required = false) boolean enabled,
            @RequestParam("role") User.Role role,
            // Address fields
            @RequestParam(value = "address.street", required = false) String street,
            @RequestParam(value = "address.city", required = false) String city,
            @RequestParam(value = "address.postalCode", required = false) String postalCode,
            @RequestParam(value = "address.state", required = false) String state,
            @RequestParam(value = "address.country", required = false) String country,
            // Other fields
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") boolean deleteImage,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("Updating user with ID: {}", id);
            
            // Get existing user
            User user = userService.getUserById(id);
            if (user == null) {
                throw new IllegalArgumentException("User not found with ID: " + id);
            }
            
            // Update basic user info
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setEnabled(enabled);
            user.setRole(role);
            
            // Create or update address
            if (user.getAddress() == null) {
                if (street != null || city != null || postalCode != null || state != null || country != null) {
                    com.rentacar.model.Address address = new com.rentacar.model.Address();
                    address.setStreet(street);
                    address.setCity(city);
                    address.setPostalCode(postalCode);
                    address.setState(state);
                    address.setCountry(country);
                    user.setAddress(address);
                }
            } else {
                user.getAddress().setStreet(street);
                user.getAddress().setCity(city);
                user.getAddress().setPostalCode(postalCode);
                user.getAddress().setState(state);
                user.getAddress().setCountry(country);
            }
            
            // Update the user
            userService.adminUpdateUser(id, user, profileImage, deleteImage, newPassword, confirmPassword);
            
            // Add a success notification
            redirectAttributes.addFlashAttribute("message", "Pomyślnie zmieniono dane użytkownika");
            return "redirect:/admin/user-management";
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Błąd aktualizacji użytkownika: " + e.getMessage());
            return "redirect:/admin/user-management/edit/" + id;
        }
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                throw new IllegalArgumentException("Invalid user Id: " + id);
            }
            
            // Toggle enabled status
            user.setEnabled(!user.isEnabled());
            userService.updateUser(user);
            
            String statusMessage = user.isEnabled() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("message", "User " + statusMessage + " successfully");
            
            return "redirect:/admin/user-management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error toggling user status: " + e.getMessage());
            return "redirect:/admin/user-management";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/user-management";
    }
}
