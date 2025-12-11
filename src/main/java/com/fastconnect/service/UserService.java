package com.fastconnect.service;

import com.fastconnect.dto.*;
import com.fastconnect.entity.User;
import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.Departments;
import com.fastconnect.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserService {

    //CRUD-forUser
    public User saveUserEntity(User user);
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> getUsersByAccountStatus(AccountStatus accountStatus, Pageable pageable);
    Optional<UserResponse> getUserByEmail(String email);
    UserResponse getUserById(Long id);

    //CRUD-forProfile
    Page<ProfileResponse> getAllProfiles(Pageable pageable);
    ProfileResponse getProfileByProfileId(Long profileId);
    ProfileResponse getProfileByUserId(Long userId);
    Optional<ProfileResponse> getProfileByFullName(String fullName);
    Optional<ProfileResponse> getProfileByRollNumber(String rollNumber);
    Page<ProfileResponse> getProfilesByDepartment(Departments departments, Pageable pageable);
    List<ProfileResponse> getProfilesByBatch(Integer year);
    Page<ProfileResponse> searchProfilesByFullName(String fullName, Pageable pageable);
    Page<ProfileResponse> searchProfilesByRollNumber(String rollNumber, Pageable pageable);
    ProfileResponse createProfile(ProfileRequest profileRequest,Long userId);
    ProfileResponse updateProfile(ProfileRequest profileRequest,Long userId);

    //Validation
    boolean existsByEmail(String email);
    boolean existsByRoleType(RoleType roleType);
    boolean existsByUserId(Long id);
    boolean existsByFullName(String fullName);
    boolean existsByRollNumber(String rollNumber);

    FacultyPageResponse createOrUpdateFacultyPage(Long userId, FacultyPageRequest request);
    FacultyPageResponse getFacultyPage(Long userId);;
    public List<ProfileResponse> getFollowersOfFaculty(Long facultyId);
    public void followFaculty(Long userId, Long facultyId);

}
