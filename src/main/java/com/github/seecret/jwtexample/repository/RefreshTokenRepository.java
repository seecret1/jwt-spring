package com.github.seecret.jwtexample.repository;

import com.github.seecret.jwtexample.entity.RefreshToken;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface RefreshTokenRepository {

    Mono<Boolean> save(RefreshToken refreshToken, Duration expTime);

    Mono<RefreshToken> getByValue(String refreshToken);
}
