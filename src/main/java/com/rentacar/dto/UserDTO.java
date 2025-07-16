package com.rentacar.dto;

import com.rentacar.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private User.Role role;
    private boolean enabled;
    private Set<ReservationDTO> reservations;
    private int reservationCount;
    private int activeReservationCount;

    // Constructor from entity
    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
        
        if (user.getReservations() != null) {
            this.reservations = user.getReservations().stream()
                    .map(ReservationDTO::new)
                    .collect(Collectors.toSet());
            
            this.reservationCount = user.getReservations().size();
            
            this.activeReservationCount = (int) user.getReservations().stream()
                    .filter(r -> r.getStatus() == com.rentacar.model.Reservation.ReservationStatus.CONFIRMED)
                    .count();
        }
    }

    // Convert to entity (partial, excludes password and relationships)
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setPhoneNumber(this.phoneNumber);
        user.setRole(this.role);
        user.setEnabled(this.enabled);
        
        return user;
    }
}
