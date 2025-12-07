package com.github.seecret.jwtexample.web.dto;

import com.github.seecret.jwtexample.entity.RoleType;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CreateUserRequest {

    private String username;

    private String password;

    private String email;

    private Set<RoleType> roles = new HashSet<>();
}
