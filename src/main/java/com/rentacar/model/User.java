package com.rentacar.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String profileImage;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private boolean enabled = true;
    
    @Embedded
    private Address address;
    
    // Change fetch type from LAZY to EAGER
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Reservation> reservations;
    
    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // Add the role with ROLE_ prefix to match Spring Security's expectations
        authorities.add(new SimpleGrantedAuthority(role.name()));
        
        // For debugging
        System.out.println("User " + email + " has authority: " + role.name());
        
        return authorities;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
