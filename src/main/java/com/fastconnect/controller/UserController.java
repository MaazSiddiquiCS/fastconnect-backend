package com.fastconnect.controller;

import com.fastconnect.dto.ProfileRequest;
import com.fastconnect.dto.ProfileResponse;
import com.fastconnect.dto.UserResponse;
import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.Departments;
import com.fastconnect.enums.RoleType;
import com.fastconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(Pageable pageable) {
        Page<UserResponse> userResponse=userService.getAllUsers(pageable);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("search-by-email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable long userId) {
        UserResponse userResponse=userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/search-by-account-status")
    public ResponseEntity<Page<UserResponse>> getUserByAccountStatus(@RequestParam AccountStatus accountStatus, Pageable pageable) {
        Page<UserResponse> userResponse=userService
                .getUsersByAccountStatus(accountStatus,pageable);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/search-by-role-type")
    public ResponseEntity <List<UserResponse>> getUserByRoleType(@RequestParam RoleType roleType, Pageable pageable) {
        List<UserResponse> userResponse=userService
                .getUsersByRoleType(roleType);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/check-id/{user_id}")
    public ResponseEntity<Boolean> existsByUserId(@PathVariable long user_id) {
        boolean exists=userService.existsByUserId(user_id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-by-email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists=userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profiles/")
    public ResponseEntity<Page<ProfileResponse>> getAllProfiles(Pageable pageable) {
        Page<ProfileResponse> userResponse=userService.getAllProfiles(pageable);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ProfileResponse> getProfileByProfileId(@PathVariable Long profileId) {
        ProfileResponse profileResponse=userService.getProfileByProfileId(profileId);
        return ResponseEntity.ok(profileResponse);
    }

    @GetMapping("/profile-by-user/{userId}")
    public ResponseEntity<ProfileResponse> getProfileByUserId(@PathVariable Long userId) {
        ProfileResponse profileResponse=userService.getProfileByUserId(userId);
        return ResponseEntity.ok(profileResponse);
    }

    @GetMapping("profile/search/fullName")
    public ResponseEntity<ProfileResponse> getProfileByFullName(@RequestParam String fullName) {
        return userService.getProfileByFullName(fullName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("profile/search/rollNumber")
    public ResponseEntity<ProfileResponse> getProfileByRollNumber(@RequestParam String rollNumber) {
        return userService.getProfileByRollNumber(rollNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("profile/filter/department")
    public ResponseEntity<Page<ProfileResponse>> getProfilesByDepartment(
            @RequestParam Departments departments,
            Pageable pageable) {
        Page<ProfileResponse> profiles = userService.getProfilesByDepartment(departments, pageable);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("profile/filter/batch")
    public ResponseEntity<List<ProfileResponse>> getProfilesByBatch(@RequestParam Integer year) {
        List<ProfileResponse> profiles = userService.getProfilesByBatch(year);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("profile/search/pattern/fullName")
    public ResponseEntity<Page<ProfileResponse>> searchProfilesByFullName(
            @RequestParam String query,
            Pageable pageable) {
        Page<ProfileResponse> profiles = userService.searchProfilesByFullName(query, pageable);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("profile/search/pattern/rollnumber")
    public ResponseEntity<Page<ProfileResponse>> searchProfilesByRollNumber(
            @RequestParam String query,
            Pageable pageable) {
        Page<ProfileResponse> profiles = userService.searchProfilesByRollNumber(query, pageable);
        return ResponseEntity.ok(profiles);
    }

    @PostMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> createProfile(
            @Valid
            @RequestBody ProfileRequest profileRequest,
            @PathVariable long userId) {
        ProfileResponse newProfile = userService.createProfile(profileRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProfile);
    }

    @PutMapping("/profile/{profileId}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid
            @RequestBody ProfileRequest profileRequest,
            @PathVariable long profileId
    ){
        ProfileResponse updatedProfile = userService.updateProfile(profileRequest, profileId);
        return ResponseEntity.ok(updatedProfile);
    }

    //TODO soft delete user
}
