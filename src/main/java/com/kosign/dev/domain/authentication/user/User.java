package com.kosign.dev.domain.authentication.user;

import com.kosign.dev.domain.authentication.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", unique = true, nullable = false)
    private String password;
    @Column(name ="email", unique = true, nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(updatable = false)
    private LocalDateTime created;

    @Column(insertable = false)
    private LocalDateTime updated;

    @Column(name = "login_attempt")
    private int loginAttempt;

    @Column(name = "statue")
    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;




}
