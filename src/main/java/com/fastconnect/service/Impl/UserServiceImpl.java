package com.fastconnect.service.Impl;

import com.fastconnect.dto.*;
import com.fastconnect.entity.FacultyFollowers;
import com.fastconnect.entity.FacultyPage;
import com.fastconnect.entity.Profile;
import com.fastconnect.entity.User;
import com.fastconnect.enums.AccountStatus;
import com.fastconnect.enums.Departments;
import com.fastconnect.enums.RoleType;
import com.fastconnect.exception.*;
import com.fastconnect.mapper.FacultyPageMapper;
import com.fastconnect.mapper.ProfileMapper;
import com.fastconnect.mapper.UserMapper;
import com.fastconnect.repository.FacultyFollowersRepository;
import com.fastconnect.repository.FacultyPageRepository;
import com.fastconnect.repository.ProfileRepository;
import com.fastconnect.repository.UserRepository;
import com.fastconnect.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;


    private final FacultyPageRepository facultyPageRepository;
    private final FacultyPageMapper facultyPageMapper;
    private final FacultyFollowersRepository facultyFollowersRepository;

    @Override
    public User saveUserEntity(User user) {
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable)
    {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByAccountStatus(AccountStatus accountStatus, Pageable pageable) {
        Page<User> users= userRepository.findByAccountStatus(accountStatus, pageable);
        return users.map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByEmail(String email) {
        Optional<User> users= userRepository.findByEmail(email);
        return users.map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    protected User getUserEntityByEmail(String email) {
        return  userRepository.findByEmail(email)
                .orElseThrow(()->new UserEmailNotFoundException(email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        Optional<User> user= userRepository.findById(id);
        return user.map(userMapper::toDTO)
                .orElseThrow(()->new UserNotFoundException(id));
    }



    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional(readOnly = true)
    public boolean existsByRoleType(RoleType roleType) {
        return userRepository.existsByRoleType(roleType);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long id) {
        return userRepository.existsByUserId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByFullName(String fullName) {
        return profileRepository.existsByFullName(fullName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRollNumber(String rollNumber) {
        return profileRepository.existsByRollNumber(rollNumber);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponse> getAllProfiles(Pageable pageable) {
        Page<Profile> profiles = profileRepository.findAll(pageable);
        return profiles.map(profileMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByProfileId(Long profileId) {
        Optional<Profile> profile= profileRepository.findById(profileId);
        return profile.map(profileMapper::toDTO)
                .orElseThrow(()->new ProfileNotFoundException(profileId));
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUserId(Long userId) {
        Optional<Profile> profile= profileRepository.findByUser_UserId(userId);
        return profile.map(profileMapper::toDTO)
                .orElseThrow(()->new ProfileNotFoundException(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileResponse> getProfileByFullName(String fullName) {
        return profileRepository.findByFullName(fullName)
                .map(profileMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileResponse> getProfileByRollNumber(String rollNumber) {
        return profileRepository.findByRollNumber(rollNumber)
                .map(profileMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponse> getProfilesByDepartment(Departments departments, Pageable pageable) {
        Page<Profile> profiles= profileRepository.findByDepartment(departments, pageable);
        return profiles.map(profileMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponse> getProfilesByBatch(Integer year) {
        List<Profile> profiles= profileRepository.findByBatch(year);
        return profileMapper.toDTOList(profiles);
    }

    @Override
    public Page<ProfileResponse> searchProfilesByFullName(String fullName, Pageable pageable) {
        Page<Profile> profiles= profileRepository.findByFullNameContaining(fullName, pageable);
        return profiles.map(profileMapper::toDTO);
    }

    @Override
    public Page<ProfileResponse> searchProfilesByRollNumber(String rollNumber, Pageable pageable) {
        Page<Profile> profiles= profileRepository.findByRollNumberContaining(rollNumber, pageable);
        return profiles.map(profileMapper::toDTO);
    }

    @Override
    public ProfileResponse createProfile(ProfileRequest profileRequest, Long userId) {
        User user= userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException(userId));
        if(profileRepository.existsByUser_UserId(userId))
        {
            throw new ProfileAlreadyExistsException(userId);
        }
        Profile profile = profileMapper.toEntity(profileRequest);
        profile.setUser(user);
        Profile savedProfile = profileRepository.save(profile);
        return profileMapper.toDTO(savedProfile);
    }

    @Override
    public ProfileResponse updateProfile(ProfileRequest profileRequest, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(()-> new ProfileNotFoundException(profileId));
        profile.setProfilePic(profileRequest.getProfilePic());
        profile.setBio(profileRequest.getBio());
        profile.setDepartment(profileRequest.getDepartment());
        profile.setFullName(profileRequest.getFullName());
        profile.setBatch(profileRequest.getBatch());
        profile.setRollNumber(profileRequest.getRollNumber());
        profile.setCoverPic(profileRequest.getCoverPic());
        Profile updatedProfile = profileRepository.save(profile);

        return profileMapper.toDTO(updatedProfile);
    }

    // --- FACULTY METHODS ---

    @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    @Override
    @Transactional
    public FacultyPageResponse createOrUpdateFacultyPage(Long userId, FacultyPageRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        FacultyPage facultyPage = facultyPageRepository.findByUserUserId(userId)
                .orElse(new FacultyPage());

        if (facultyPage.getUser() == null) {
            facultyPage.setUser(user);
        }

        facultyPageMapper.updateEntity(facultyPage, request);

        FacultyPage savedPage = facultyPageRepository.save(facultyPage);
        return facultyPageMapper.toDTO(savedPage);
    }

    @Override
    @Transactional(readOnly = true)
    public FacultyPageResponse getFacultyPage(Long userId) {
        FacultyPage facultyPage = facultyPageRepository.findByUserUserId(userId)
                .orElseThrow(() -> new com.fastconnect.exception.FacultyPageNotFoundException(userId));

        return facultyPageMapper.toDTO(facultyPage);
    }

    public void followFaculty(Long userId, Long facultyId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        FacultyPage faculty = facultyPageRepository.findById(facultyId)
                .orElseThrow(() -> new FacultyPageNotFoundException(facultyId));

        if (facultyFollowersRepository.existsByUserAndFacultyPage(user, faculty)) {
            throw new AlreadyFollowingException("You are already following this faculty/society");
        }

        FacultyFollowers follower = new FacultyFollowers();
        follower.setUser(user);
        follower.setFacultyPage(faculty);

        facultyFollowersRepository.save(follower);
    }
    public List<ProfileResponse> getFollowersOfFaculty(Long facultyId) {

        FacultyPage faculty = facultyPageRepository.findById(facultyId)
                .orElseThrow(() -> new FacultyPageNotFoundException(facultyId));

        List<FacultyFollowers> followers = facultyFollowersRepository.findAllByFacultyPage(faculty);

        // convert User → ProfileResponse
        return followers.stream()
                .map(f -> profileMapper.toDTO(f.getUser().getProfile()))
                .toList();
    }

}