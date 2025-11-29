//package com.fastconnect.repository;
//
//import com.fastconnect.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface UserRepository extends JpaRepository<User,Long> {
//
//    Optional<User> findByProfile_FullName(String fullName);
//
//    Optional<User> findByEmail(String email);
//
//    boolean existsByfull_name(String username);
//
//    boolean existsByEmail(String email);
//
//}
