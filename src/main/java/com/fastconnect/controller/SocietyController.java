package com.fastconnect.controller;

import com.fastconnect.dto.ProfileResponse;
import com.fastconnect.dto.SocietyMembershipResponse;
import com.fastconnect.dto.SocietyRequest;
import com.fastconnect.dto.SocietyResponse;
import com.fastconnect.enums.SocietyRoles;
import com.fastconnect.security.CustomUserDetails;
import com.fastconnect.service.SocietyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/societies")
@CrossOrigin(origins = "*")
@Tag(name = "Society Management & Interaction", description = "Endpoints for creating, managing, joining, and following societies.")
public class SocietyController {

    @Autowired
    private SocietyService societyService;

    @Operation(
            summary = "Create a new society.",
            description = "Registers a new society. The authenticated creator is automatically set as the PRESIDENT. Requires ADMIN or SOCIETY_ADMIN role.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Society created successfully."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict: Society name already exists (SocietyAlreadyExistsException)."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Insufficient role."
                    )
            }
    )
    @PostMapping("/create-society")
    public ResponseEntity<SocietyResponse> createSociety(
            @Valid @RequestBody SocietyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        SocietyResponse response = societyService.createSociety(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Retrieve a paginated list of all societies.",
            description = "Publicly accessible list of all registered societies.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list."
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<Page<SocietyResponse>> getAllSocieties(Pageable pageable) {
        Page<SocietyResponse> response = societyService.getAllSocieties(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Retrieve a society by its ID.",
            description = "Returns details for a specific society. Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Society found."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society not found (SocietyNotFoundException)."
                    )
            }
    )
    @GetMapping("/{societyId}")
    public ResponseEntity<SocietyResponse> getSocietyById(@PathVariable Long societyId) {
        SocietyResponse response = societyService.getSocietyById(societyId);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Update an existing society's details.",
            description = "Updates editable fields of a society. Requires ADMIN role OR the user must be the PRESIDENT of the society.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Society updated successfully."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Not the society President or Admin."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict: Updated name already exists."
                    )
            }
    )
    @PutMapping("/update/{societyId}")
    public ResponseEntity<SocietyResponse> updateSociety(
            @PathVariable Long societyId,
            @Valid @RequestBody SocietyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        SocietyResponse response = societyService.updateSociety(societyId, request, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Join a society.",
            description = "Adds the authenticated user as an active MEMBER of the society.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully joined or reactivated membership."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society not found."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict: User is already an active member (SocietyAlreadyExistsException)."
                    )
            }
    )
    @PostMapping("/{societyId}/join")
    public ResponseEntity<SocietyMembershipResponse> joinSociety(
            @PathVariable Long societyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        SocietyMembershipResponse response = societyService.joinSociety(societyId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Leave a society.",
            description = "Sets the authenticated user's membership to inactive (leaving the society).",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully left the society (No Content)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society not found, or user was never a member (SocietyNotFoundException)."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict: User has already left the society."
                    )
            }
    )
    @PostMapping("/{societyId}/leave")
    public ResponseEntity<Void> leaveSociety(
            @PathVariable Long societyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        societyService.leaveSociety(societyId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Change the role of a society member.",
            description = "Assigns a new role (e.g., SECRETARY, MEMBER) to a member. Requires ADMIN role OR the user must be the PRESIDENT of the society.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Role updated successfully."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society or target user/membership not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Insufficient role."
                    )
            }
    )
    @PutMapping("/change-membership/{societyId}/members/{memberId}/role")
    public ResponseEntity<SocietyMembershipResponse> changeMemberRole(
            @PathVariable Long societyId,
            @PathVariable Long memberId,
            @RequestParam SocietyRoles newRole,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        SocietyMembershipResponse response = societyService.changeMemberRole(societyId, memberId, newRole, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get a list of active society members.",
            description = "Returns a list of all currently active members (excluding followers). Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of members."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society not found."
                    )
            }
    )
    @GetMapping("/{societyId}/members")
    public ResponseEntity<List<SocietyMembershipResponse>> getSocietyMembers(@PathVariable Long societyId) {
        List<SocietyMembershipResponse> members = societyService.getSocietyMembers(societyId);
        return ResponseEntity.ok(members);
    }


    @Operation(
            summary = "Follow a society.",
            description = "Creates a follower record for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Followed successfully (No Content)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society not found."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict: User is already following this society (SocietyAlreadyExistsException)."
                    )
            }
    )
    @PostMapping("/{societyId}/follow")
    public ResponseEntity<Void> followSociety(
            @PathVariable Long societyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        societyService.followSociety(societyId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Unfollow a society.",
            description = "Removes the follower record for the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Unfollowed successfully (No Content)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society not found, or follower record did not exist (SocietyNotFoundException)."
                    )
            }
    )
    @DeleteMapping("/{societyId}/unfollow")
    public ResponseEntity<Void> unfollowSociety(
            @PathVariable Long societyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        societyService.unfollowSociety(societyId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get a list of profiles following a society.",
            description = "Returns the profile details of all users who follow the given society. Publicly accessible.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of followers."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Society not found."
                    )
            }
    )
    @GetMapping("/{societyId}/followers")
    public ResponseEntity<List<ProfileResponse>> getSocietyFollowers(@PathVariable Long societyId) {
        List<ProfileResponse> followers = societyService.getSocietyFollowers(societyId);
        return ResponseEntity.ok(followers);
    }
}