package com.kosign.dev.service.user;

import com.kosign.dev.domain.authentication.role.Role;
import com.kosign.dev.domain.authentication.role.RoleRepository;
import com.kosign.dev.domain.authentication.user.User;
import com.kosign.dev.domain.authentication.user.UserRepository;
import com.kosign.dev.payload.user.UserRequest;
import com.kosign.dev.payload.user.UserResponse;
import com.kosign.dev.utils.Constant;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserMapper {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserMapper(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    public User mapToUserEntity(UserRequest payload){
        List<Role> roles = roleRepository.findAllByNameIn(payload.roles().stream().map(Role::getName).toList());
        return User.builder()
                .username(payload.username())
                .password(payload.password())
                .email(payload.email())
                .fullName(payload.fullName())
                .roles(roles)
                .loginAttempt(0)
                .status(Constant.ACT)
                .created(LocalDateTime.now())
                .build();
    }

    public UserResponse mapToUserResponse(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .created(user.getCreated())
                .build();
    }
}
