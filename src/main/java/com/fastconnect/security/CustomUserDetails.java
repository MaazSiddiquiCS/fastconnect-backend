package com.fastconnect.security; // Or a dedicated security package

import com.fastconnect.entity.User; // Assuming you have a User entity
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails {

    // --- NEW GETTER FOR USER ID ---
    @Getter
    private final Long userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    // NOTE: You don't need the actual password field here since you aren't using it for authentication later.

    public CustomUserDetails(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleType().name()));
    }

    // --- REQUIRED USERDETAILS METHODS ---
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

}