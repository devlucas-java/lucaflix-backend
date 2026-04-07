package com.lucaflix.security;

import com.lucaflix.exception.InvalidCredentialsException;
import com.lucaflix.model.User;
import com.lucaflix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(InvalidCredentialsException::new);
    }
}