package com.rentacar.controller;

import com.rentacar.model.Reservation;
import com.rentacar.model.User;
import com.rentacar.service.ReservationService;
import com.rentacar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;
    private final ReservationService reservationService;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsernameWithReservations(auth.getName());
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        model.addAttribute("user", user);
        return "user/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @ModelAttribute User userForm,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false, defaultValue = "false") boolean deleteImage,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            @RequestParam(required = false) String currentPassword,
            RedirectAttributes redirectAttributes) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(auth.getName());
            
            // Security check - only allow users to update their own profile
            if (!currentUser.getId().equals(userForm.getId())) {
                throw new SecurityException("Unauthorized profile update attempt");
            }
            
            // Update the user profile
            userService.updateUserProfile(
                currentUser.getId(),
                userForm,
                profileImage,
                deleteImage,
                newPassword,
                confirmPassword,
                currentPassword
            );
            
            // Force re-authentication with updated details
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userService.loadUserByUsername(userForm.getEmail()),
                null,
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            );
            
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            
            redirectAttributes.addFlashAttribute("success", "Profil zaktualizowany pomyślnie");
            return "redirect:/user/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile/edit";
        }
    }

    @GetMapping("/reservations")
    public String myReservations(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userService.loadUserByUsername(auth.getName());

        List<Reservation> reservations = reservationService.getReservationsByUser(user);
        model.addAttribute("reservations", reservations);

        return "user/reservations";
    }
}
