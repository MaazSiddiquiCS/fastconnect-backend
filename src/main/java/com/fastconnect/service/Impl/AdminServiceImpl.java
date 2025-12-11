package com.fastconnect.service.Impl;

import com.fastconnect.dto.UserResponse;
import com.fastconnect.entity.User;
import com.fastconnect.enums.RoleType;
import com.fastconnect.exception.UserEmailNotFoundException;
import com.fastconnect.exception.UserNotFoundException;
import com.fastconnect.mapper.UserMapper;
import com.fastconnect.repository.UserRepository;
import com.fastconnect.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException(userId));
        userRepository.delete(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRoleType(RoleType roleType) {
        List<User> users =userRepository.findByRoleType(roleType);
        return userMapper.toDTOList(users);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse changeUserRole(String email, RoleType newRole) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(()-> new UserEmailNotFoundException(email));
        user.setRoleType(newRole);
        return userMapper.toDTO(userRepository.save(user));
    }
}
