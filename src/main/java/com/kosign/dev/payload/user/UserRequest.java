package com.kosign.dev.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kosign.dev.domain.authentication.role.Role;

import java.util.List;

public record UserRequest (
        String username,
        String password,
        String email,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("roles") List<Role> roles
){
}
