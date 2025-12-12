package com.fastconnect.service;

import com.fastconnect.dto.*;
import com.fastconnect.enums.SocietyCategory;
import com.fastconnect.enums.SocietyRoles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SocietyService {

    SocietyResponse createSociety(SocietyRequest request, Long currentUserId);

    SocietyResponse updateSociety(Long societyId, SocietyRequest request, Long currentUserId);

    SocietyResponse getSocietyById(Long societyId);

    Page<SocietyResponse> getAllSocieties(Pageable pageable);

    Page<SocietyResponse> getSocietiesByCategory(SocietyCategory category, Pageable pageable);


    SocietyMembershipResponse joinSociety(Long societyId, Long currentUserId);

    void leaveSociety(Long societyId, Long currentUserId);

    SocietyMembershipResponse changeMemberRole(Long societyId, Long memberUserId, SocietyRoles newRole, Long currentUserId);

    List<SocietyMembershipResponse> getSocietyMembers(Long societyId);

    void followSociety(Long societyId, Long currentUserId);

    void unfollowSociety(Long societyId, Long currentUserId);

    List<ProfileResponse> getSocietyFollowers(Long societyId);

    boolean isFollowing(Long societyId, Long currentUserId);
}