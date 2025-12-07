package com.github.seecret.jwtexample.security;

import com.github.seecret.jwtexample.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveAuthentificationManagerImpl implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        AppUserPrincipal userPrincipal = (AppUserPrincipal) authentication.getPrincipal();
        return userDetailsService.findByUsername(userPrincipal.getName())
                .filter(UserDetails::isEnabled)
                .switchIfEmpty(Mono.error(new AuthException("User disabled")))
                .map(user -> authentication);
    }
}
