package com.fastconnect.controller;

import com.fastconnect.dto.ProfileRequest;
import com.fastconnect.dto.ProfileResponse;
import com.fastconnect.dto.UserResponse;
import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.Departments;
import com.fastconnect.enums.RoleType;
import com.fastconnect.security.CustomUserDetails;
import com.fastconnect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import com.fastconnect.dto.FacultyPageRequest;
import com.fastconnect.dto.FacultyPageResponse;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User, Faculty Page & Profile Management", description = "Endpoints for user CRUD, profile creation/updates, and basic search/filter operations.")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(
            summary = "Retrieve a paginated list of all users.",
            description = "Retrieves all user records. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of users."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User does not have the ADMIN role."
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<Page<UserResponse>> findAll(Pageable pageable) {
        Page<UserResponse> userResponse=userService.getAllUsers(pageable);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(
            summary = "Search for a user by email address.",
            description = "Returns user details if the email is found. Publicly accessible for search functionality.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found."
                    )
            }
    )
    @GetMapping("search-by-email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Retrieve the current authenticated user's details.",
            description = "Uses the JWT in the request to return the details of the logged-in user. Requires authentication.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved current user's details."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized: Missing or invalid token."
                    )
            }
    )
    @GetMapping("/")
    public ResponseEntity<UserResponse> getUserById(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserResponse userResponse=userService.getUserById(customUserDetails.getUserId());
        return ResponseEntity.ok(userResponse);
    }

    @Operation(
            summary = "Filter users by Account Status.",
            description = "Retrieves users based on their account status (e.g., ACTIVE, PENDING, BLOCKED). Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the filtered list."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Requires ADMIN role."
                    )
            }
    )
    @GetMapping("/search-by-account-status")
    public ResponseEntity<Page<UserResponse>> getUserByAccountStatus(@RequestParam AccountStatus accountStatus, Pageable pageable) {
        Page<UserResponse> userResponse=userService
                .getUsersByAccountStatus(accountStatus,pageable);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(
            summary = "Check if a user ID exists.",
            description = "Returns true if the user ID is found in the database. Requires ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Returns true/false."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Requires ADMIN role."
                    )
            }
    )
    @GetMapping("/check-id/{user_id}")
    public ResponseEntity<Boolean> existsByUserId(@PathVariable long user_id) {
        boolean exists=userService.existsByUserId(user_id);
        return ResponseEntity.ok(exists);
    }
    @Operation(
            summary = "Check if an email is already registered.",
            description = "Returns true if the email exists. Used typically during registration. Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Returns true/false."
                    )
            }
    )
    @GetMapping("/check-by-email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists=userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @Operation(
            summary = "Retrieve a paginated list of all public profiles.",
            description = "Lists all created user profiles. Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of profiles."
                    )
            }
    )
    @GetMapping("/profiles/")
    public ResponseEntity<Page<ProfileResponse>> getAllProfiles(Pageable pageable) {
        Page<ProfileResponse> userResponse=userService.getAllProfiles(pageable);
        return ResponseEntity.ok(userResponse);
    }


    @Operation(
            summary = "Get a profile by its Profile ID.",
            description = "Retrieves a specific profile using its unique Profile ID. Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile found."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Profile not found with the given ID."
                    )
            }
    )
    @GetMapping("/profile/{profileId}")
    public ResponseEntity<ProfileResponse> getProfileByProfileId(@PathVariable Long profileId) {
        ProfileResponse profileResponse=userService.getProfileByProfileId(profileId);
        return ResponseEntity.ok(profileResponse);
    }


    @Operation(
            summary = "Get a profile by its associated User ID.",
            description = "Retrieves a specific profile using the ID of the related User entity. Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile found."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Profile not found for the given User ID."
                    )
            }
    )
    @GetMapping("/profile-by-user/{userId}")
    public ResponseEntity<ProfileResponse> getProfileByUserId(@PathVariable Long userId) {
        ProfileResponse profileResponse=userService.getProfileByUserId(userId);
        return ResponseEntity.ok(profileResponse);
    }

    @Operation(
            summary = "Search for a profile by exact Full Name match.",
            description = "Performs an exact match search on the profile's full name field. Publicly accessible.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile found."),
                    @ApiResponse(responseCode = "404", description = "Profile not found.")
            }
    )
    @GetMapping("profile/search/fullName")
    public ResponseEntity<ProfileResponse> getProfileByFullName(@RequestParam String fullName) {
        return userService.getProfileByFullName(fullName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Search for a profile by exact Roll Number match.",
            description = "Performs an exact match search on the profile's roll number field. Publicly accessible.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile found."),
                    @ApiResponse(responseCode = "404", description = "Profile not found.")
            }
    )
    @GetMapping("profile/search/rollNumber")
    public ResponseEntity<ProfileResponse> getProfileByRollNumber(@RequestParam String rollNumber) {
        return userService.getProfileByRollNumber(rollNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Filter profiles by academic department.",
            description = "Retrieves a paginated list of profiles belonging to the specified department. Publicly accessible.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered profiles.")
            }
    )
    @GetMapping("profile/filter/department")
    public ResponseEntity<Page<ProfileResponse>> getProfilesByDepartment(
            @RequestParam Departments departments,
            Pageable pageable) {
        Page<ProfileResponse> profiles = userService.getProfilesByDepartment(departments, pageable);
        return ResponseEntity.ok(profiles);
    }


    @Operation(
            summary = "Filter profiles by graduation/joining batch year.",
            description = "Retrieves a list of profiles for the specified batch year. Publicly accessible.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved profiles for the batch.")
            }
    )
    @GetMapping("profile/filter/batch")
    public ResponseEntity<List<ProfileResponse>> getProfilesByBatch(@RequestParam Integer year) {
        List<ProfileResponse> profiles = userService.getProfilesByBatch(year);
        return ResponseEntity.ok(profiles);
    }


    @Operation(
            summary = "Search profiles by partial Full Name match.",
            description = "Performs a 'contains' search on the profile's full name. Publicly accessible.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching profiles.")
            }
    )
    @GetMapping("profile/search/pattern/fullName")
    public ResponseEntity<Page<ProfileResponse>> searchProfilesByFullName(
            @RequestParam String query,
            Pageable pageable) {
        Page<ProfileResponse> profiles = userService.searchProfilesByFullName(query, pageable);
        return ResponseEntity.ok(profiles);
    }


    @Operation(
            summary = "Search profiles by partial Roll Number match.",
            description = "Performs a 'contains' search on the profile's roll number. Publicly accessible.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching profiles.")
            }
    )
    @GetMapping("profile/search/pattern/rollnumber")
    public ResponseEntity<Page<ProfileResponse>> searchProfilesByRollNumber(
            @RequestParam String query,
            Pageable pageable) {
        Page<ProfileResponse> profiles = userService.searchProfilesByRollNumber(query, pageable);
        return ResponseEntity.ok(profiles);
    }


    @Operation(
            summary = "Create the current user's profile.",
            description = "Creates a detailed profile associated with the currently authenticated user. Requires authentication. Fails if a profile already exists for the user.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Profile successfully created."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict: Profile already exists for this user."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized: Missing or invalid token."
                    )
            }
    )
    @PostMapping("/profile")
    public ResponseEntity<ProfileResponse> createProfile(
            @Valid
            @RequestBody ProfileRequest profileRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ProfileResponse newProfile = userService.createProfile(profileRequest, customUserDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newProfile);
    }


    @Operation(
            summary = "Update an existing user profile.",
            description = "Updates the fields of an existing profile. Access control is assumed to restrict this to the profile owner or an ADMIN.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile successfully updated."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Profile not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User does not own the profile."
                    )
            }
    )
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

    @Operation(
            summary = "Create or update a faculty/society page.",
            description = "Allows a FACULTY user to create or update their public page. Requires FACULTY or ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Faculty page created or updated successfully."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Requires FACULTY or ADMIN role."
                    )
            }
    )
    @PostMapping("/faculty-page")// Security Check
    public ResponseEntity<FacultyPageResponse> createFacultyPage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody FacultyPageRequest request) {
        return ResponseEntity.ok(userService.createOrUpdateFacultyPage(customUserDetails.getUserId(), request));
    }

    // 2. Get Faculty Page (Public)

    @Operation(
            summary = "Retrieve a faculty/society page by the owner's User ID.",
            description = "Retrieves the public page content. Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Faculty page retrieved."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Faculty page not found for this user."
                    )
            }
    )
    @GetMapping("/faculty-page/{userId}")
    public ResponseEntity<FacultyPageResponse> getFacultyPage(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFacultyPage(userId));
    }


    @Operation(
            summary = "Follow a specific faculty/society page.",
            description = "Creates a follower relationship between the authenticated user and the faculty page. Requires authentication.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Followed successfully."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict: Already following this page."
                    )
            }
    )
    @PostMapping("/faculty/{facultyId}/follow")
    public ResponseEntity<String> followFaculty(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long facultyId
    ) {
        userService.followFaculty(user.getUserId(), facultyId);
        return ResponseEntity.ok("Followed successfully.");
    }

    @Operation(
            summary = "Retrieve the list of profiles following a faculty/society page.",
            description = "Returns the profile details of all users who follow the given page. Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of followers."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Faculty page not found."
                    )
            }
    )
    @GetMapping("/faculty/{facultyId}/followers")
    public ResponseEntity<List<ProfileResponse>> getFollowers(@PathVariable Long facultyId) {
        return ResponseEntity.ok(userService.getFollowersOfFaculty(facultyId));
    }

}