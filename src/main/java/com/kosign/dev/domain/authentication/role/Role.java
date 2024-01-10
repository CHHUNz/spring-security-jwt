package com.kosign.dev.domain.authentication.role;

import com.kosign.dev.domain.authentication.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_Roles")

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long role_id;

    @Column(name = "role_name")
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
}
