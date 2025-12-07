package com.fastconnect.repository;

import com.fastconnect.entity.Post;
import com.fastconnect.entity.Reaction;
import com.fastconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByPostAndUser(Post post, User user);
}