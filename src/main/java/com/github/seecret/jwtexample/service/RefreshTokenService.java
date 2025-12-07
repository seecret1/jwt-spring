package com.github.seecret.jwtexample.service;

import com.github.seecret.jwtexample.entity.RefreshToken;
import reactor.core.publisher.Mono;

public interface RefreshTokenService {

    Mono<RefreshToken> save(String userId);

    Mono<RefreshToken> getByValue(String refreshToken);
}
