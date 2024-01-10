package com.kosign.dev.service.user;

import com.kosign.dev.domain.authentication.role.Role;
import com.kosign.dev.domain.authentication.role.RoleRepository;
import com.kosign.dev.domain.authentication.user.User;
import com.kosign.dev.domain.authentication.user.UserRepository;
import com.kosign.dev.exception.InternalServerErrorException;
import com.kosign.dev.exception.NotFoundExceptionClass;
import com.kosign.dev.exception.NullExceptionClass;
import com.kosign.dev.payload.user.ResponseErrorTemplate;
import com.kosign.dev.payload.user.UserRequest;
import com.kosign.dev.payload.user.UserResponse;
import com.kosign.dev.utils.Constant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    @Override
    public void create(UserRequest userRequest) {
        this.userRequestValidation(userRequest);
        var entity = userMapper.mapToUserEntity(userRequest);
        userRepository.save(entity);
    }

    private void userRequestValidation(UserRequest userRequest) {
        //TODO: 01/10/24 validation password
        if (ObjectUtils.isEmpty(userRequest.password())){
            log.warn("password must be not null or blank");
            throw  new NullExceptionClass("Password must be not null or blank","Password");
        }

        //TODO: 01/10/24 validation email or username duplicate
        Optional<User> user = userRepository.findAllByUsernameOrEmail(userRequest.username(), userRequest.email());
        if (user.isPresent()){
            log.warn("Username or Email can't be duplicate");
            throw new InternalServerErrorException("Username or Email can't be duplicate");
        }

        //TODO: 01/10/24 validation roles
        List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
        for (var role:userRequest.roles()){
            if (!roles.contains(role)){
                log.warn("Role is invalid request.");
                throw new NotFoundExceptionClass("Role is not found");
            }
        }


    }

    @Override
    public Object findAllById(Long id) {
        return  userRepository.findById(id)
                .map(userMapper::mapToUserResponse)
                .orElseThrow(()-> new NotFoundExceptionClass("User not found"));
    }

    @Override
    public Object findByUsername(String username) {
        return userRepository.findFirstByUsernameAndStatus(username, Constant.ACT)
                .map(userMapper::mapToUserResponse)
                .orElseThrow(()-> new NotFoundExceptionClass("Username not found"));
    }
}
