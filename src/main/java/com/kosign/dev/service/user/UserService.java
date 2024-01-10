package com.kosign.dev.service.user;

import com.kosign.dev.domain.authentication.user.User;
import com.kosign.dev.payload.user.UserRequest;

public interface UserService {

    void create(UserRequest userRequest);
    Object findAllById(Long id);
    Object findByUsername(String username);
}
