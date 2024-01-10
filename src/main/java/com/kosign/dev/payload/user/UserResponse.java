package com.kosign.dev.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kosign.dev.domain.authentication.role.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class UserResponse {
    String username;
    String password;
    String email;
    @JsonProperty("full_name") String fullName;
    @JsonProperty("roles")
    List<String> roles;
    private String status;
    @JsonProperty("login_attempt") int loginAttempt;
    private LocalDateTime created;
    private LocalDateTime updated;

    public UserResponse(String username, String password, String email, String fullname, List<String> roles, String status, int loginAttempt, LocalDateTime created, LocalDateTime updated){
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullname;
        this.roles = roles;
        this.loginAttempt = loginAttempt;
        this.created = created;
        this.updated = updated;
    }


}
