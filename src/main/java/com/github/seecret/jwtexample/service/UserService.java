package com.github.seecret.jwtexample.service;

import com.github.seecret.jwtexample.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> create(User user);

    Mono<User> findByUsername(String username);

    Mono<User> findById(String id);
}
