package com.github.seecret.jwtexample.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user")
public class User {

    @Id
    @Indexed
    private String id;

    @Indexed
    private String username;

    private String email;

    private String password;

    private Set<RoleType> roles = new HashSet<>();
}
