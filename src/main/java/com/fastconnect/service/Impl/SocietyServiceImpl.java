package com.fastconnect.service.Impl;

import com.fastconnect.dto.*;
import com.fastconnect.entity.*;
import com.fastconnect.enums.SocietyCategory;
import com.fastconnect.enums.SocietyRoles;
import com.fastconnect.exception.*; // Import new exceptions
import com.fastconnect.mapper.*;
import com.fastconnect.repository.*;
import com.fastconnect.service.SocietyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class SocietyServiceImpl implements SocietyService {


    private final SocietyRepository societyRepository;
    private final UserRepository userRepository;
    private final SocietyMembershipRepository membershipRepository;
    private final SocietyFollowersRepository followersRepository;
    private final ProfileRepository profileRepository;

    private final SocietyMapper societyMapper;
    private final SocietyMembershipMapper membershipMapper;
    private final SocietyFollowersMapper followersMapper;
    private final ProfileMapper profileMapper;

    // --- UTILITY METHODS ---

    private Society getSocietyEntityById(Long societyId) {
        // Updated to use SocietyNotFoundException
        return societyRepository.findById(societyId)
                .orElseThrow(() -> new SocietyNotFoundException(societyId));
    }

    private User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }


    // --- SOCIETY CRUD & ACCESS ---

    @PreAuthorize("hasAnyRole('ADMIN','SOCIETY_ADMIN')")
    @Override
    @Transactional
    public SocietyResponse createSociety(SocietyRequest request, Long currentUserId) {
        // 1. Check for unique name
        if (societyRepository.existsBySocietyName(request.getSocietyName())) {
            // Updated to use SocietyAlreadyExistsException
            throw new SocietyAlreadyExistsException("Society with name '" + request.getSocietyName() + "' already exists.");
        }

        User creator = getUserEntityById(currentUserId);

        // 2. Create and save Society
        Society society = societyMapper.toEntity(request);
        Society savedSociety = societyRepository.save(society);

        // 3. Automatically add creator as PRESIDENT
        SocietyMembership presidentMembership = new SocietyMembership();
        presidentMembership.setSociety(savedSociety);
        presidentMembership.setUser(creator);
        presidentMembership.setSocietyRole(SocietyRoles.PRESIDENT);
        presidentMembership.setActive(true);
        membershipRepository.save(presidentMembership);

        return societyMapper.toDTO(savedSociety);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SOCIETY_ADMIN')")
    @Override
    @Transactional
    public SocietyResponse updateSociety(Long societyId, SocietyRequest request, Long currentUserId) {
        Society society = getSocietyEntityById(societyId);

        // Check if the new name conflicts with any other society (excluding itself)
        if (societyRepository.findBySocietyName(request.getSocietyName())
                .filter(s -> !s.getSocietyId().equals(societyId))
                .isPresent()) {
            throw new SocietyAlreadyExistsException("Another society with name '" + request.getSocietyName() + "' already exists.");
        }

        societyMapper.updateEntity(society, request);
        Society updatedSociety = societyRepository.save(society);
        return societyMapper.toDTO(updatedSociety);
    }

    @Override
    @Transactional(readOnly = true)
    public SocietyResponse getSocietyById(Long societyId) {
        Society society = getSocietyEntityById(societyId);
        return societyMapper.toDTO(society);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SocietyResponse> getAllSocieties(Pageable pageable) {
        return societyRepository.findAll(pageable)
                .map(societyMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SocietyResponse> getSocietiesByCategory(SocietyCategory category, Pageable pageable) {
        return societyRepository.findByCategory(category, pageable)
                .map(societyMapper::toDTO);
    }


    // --- SOCIETY MEMBERSHIP (JOIN/LEAVE/ROLE) ---

    @Override
    @Transactional
    public SocietyMembershipResponse joinSociety(Long societyId, Long currentUserId) {
        Society society = getSocietyEntityById(societyId);
        User user = getUserEntityById(currentUserId);

        // Check if user is already an active member
        if (membershipRepository.existsBySocietyAndUserAndActiveTrue(society, user)) {
            // Updated to use SocietyAlreadyExistsException
            throw new AlreadyFollowingException("already joined society");
        }

        // Create or reactivate membership
        Optional<SocietyMembership> existingMembership = membershipRepository.findBySocietyAndUser(society, user);
        SocietyMembership membership;

        if (existingMembership.isPresent()) {
            membership = existingMembership.get();
            membership.setActive(true);
        } else {
            membership = new SocietyMembership();
            membership.setSociety(society);
            membership.setUser(user);
            membership.setSocietyRole(SocietyRoles.MEMBER);
        }

        SocietyMembership savedMembership = membershipRepository.save(membership);
        return membershipMapper.toDTO(savedMembership);
    }

    @Override
    @Transactional
    public void leaveSociety(Long societyId, Long currentUserId) {
        Society society = getSocietyEntityById(societyId);
        User user = getUserEntityById(currentUserId);

        SocietyMembership membership = membershipRepository.findBySocietyAndUser(society, user)
                // If membership doesn't exist, treat it as not found
                .orElseThrow(() -> new SocietyNotFoundException("Membership not found for user " + currentUserId + " in society " + societyId));

        if (membership.getActive()) {
            membership.setActive(false);
            membershipRepository.save(membership);
        } else {
            // throw that the user already left
            throw new SocietyAlreadyExistsException("User has already left this society.");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','SOCIETY_ADMIN')")
    @Override
    @Transactional
    public SocietyMembershipResponse changeMemberRole(Long societyId, Long memberUserId, SocietyRoles newRole, Long currentUserId) {
        Society society = getSocietyEntityById(societyId);
        User memberUser = getUserEntityById(memberUserId);

        SocietyMembership membership = membershipRepository.findBySocietyAndUser(society, memberUser)
                // If membership doesn't exist, treat it as not found
                .orElseThrow(() -> new SocietyNotFoundException("Target user is not a member of society " + societyId));

        membership.setSocietyRole(newRole);

        SocietyMembership updatedMembership = membershipRepository.save(membership);
        return membershipMapper.toDTO(updatedMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocietyMembershipResponse> getSocietyMembers(Long societyId) {
        Society society = getSocietyEntityById(societyId);
        List<SocietyMembership> memberships = membershipRepository.findBySocietyAndActiveTrue(society);
        return membershipMapper.toDTOList(memberships);
    }


    // --- SOCIETY FOLLOWERS (FOLLOW/UNFOLLOW) ---

    @Override
    @Transactional
    public void followSociety(Long societyId, Long currentUserId) {
        Society society = getSocietyEntityById(societyId);
        User user = getUserEntityById(currentUserId);

        if (followersRepository.existsBySocietyAndUser(society, user)) {
            throw new AlreadyFollowingException("Already following Society"+society.getSocietyName());
        }

        SocietyFollowers follower = new SocietyFollowers();
        follower.setSociety(society);
        follower.setUser(user);

        followersRepository.save(follower);
    }

    @Override
    @Transactional
    public void unfollowSociety(Long societyId, Long currentUserId) {
        Society society = getSocietyEntityById(societyId);
        User user = getUserEntityById(currentUserId);

        SocietyFollowers follower = followersRepository.findBySocietyAndUser(society, user)
                // If follower record doesn't exist, treat it as not found
                .orElseThrow(() -> new SocietyNotFoundException("Follower record not found for user " + currentUserId + " on society " + societyId));

        followersRepository.delete(follower);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponse> getSocietyFollowers(Long societyId) {
        Society society = getSocietyEntityById(societyId);

        List<SocietyFollowers> followers = followersRepository.findBySociety(society);

        return followers.stream()
                .map(f -> f.getUser().getProfile())
                .map(profileMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long societyId, Long currentUserId) {
        Society society = getSocietyEntityById(societyId);
        User user = getUserEntityById(currentUserId);
        return followersRepository.existsBySocietyAndUser(society, user);
    }

    @Transactional(readOnly = true)
    public boolean isUserPresident(Long societyId, Long currentUserId) {
        Optional<SocietyMembership> membership = membershipRepository.findBySocietyAndUser(
                getSocietyEntityById(societyId),
                getUserEntityById(currentUserId)
        );

        return membership.isPresent() &&
                membership.get().getActive() &&
                membership.get().getSocietyRole() == SocietyRoles.PRESIDENT;
    }
}