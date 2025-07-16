package com.rentacar.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class SimpleAuthoritiesMapper implements GrantedAuthoritiesMapper {
    
    private static final Logger logger = Logger.getLogger(SimpleAuthoritiesMapper.class.getName());

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        for (GrantedAuthority authority : authorities) {
            // Log the authority for debugging
            logger.info("Original authority: " + authority.getAuthority());
            
            // Always add the original authority
            mappedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
            
            // For legacy compatibility, also add without ROLE_ prefix if it exists
            if (authority.getAuthority().startsWith("ROLE_")) {
                String unprefixed = authority.getAuthority().substring(5);
                mappedAuthorities.add(new SimpleGrantedAuthority(unprefixed));
                logger.info("Added unprefixed authority: " + unprefixed);
            }
            // For forward compatibility, also add with ROLE_ prefix if it doesn't exist
            else if (!authority.getAuthority().startsWith("ROLE_")) {
                String prefixed = "ROLE_" + authority.getAuthority();
                mappedAuthorities.add(new SimpleGrantedAuthority(prefixed));
                logger.info("Added prefixed authority: " + prefixed);
            }
        }
        
        // Log all mapped authorities
        for (GrantedAuthority authority : mappedAuthorities) {
            logger.info("Mapped authority: " + authority.getAuthority());
        }
        
        return mappedAuthorities;
    }
}
