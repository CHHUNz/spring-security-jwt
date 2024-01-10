package com.kosign.dev.domain.authentication.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByUsernameAndStatus(String username, String status);
    Optional<User> findAllByUsernameOrEmail(String username, String email);
}
