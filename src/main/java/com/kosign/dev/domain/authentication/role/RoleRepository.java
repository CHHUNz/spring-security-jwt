package com.kosign.dev.domain.authentication.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findFirstByName(String name);
    List<Role> findAllByNameIn(List<String> names);
}
