package com.fastconnect.service; // Or your preferred service package

import com.fastconnect.entity.User;
import com.fastconnect.repository.UserRepository;
import com.fastconnect.security.CustomUserDetails; // Import your custom class
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // <-- This is CRITICAL for Spring to find this bean
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Fetch the raw User entity from the database using the email provided during login
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }


        // 2. Return your CustomUserDetails object, which contains the userId and authorities
        return new CustomUserDetails(user);
    }
}