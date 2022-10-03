package ru.ahmed.anam.todoservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.ahmed.anam.todoservice.domain.User;
import ru.ahmed.anam.todoservice.repositories.UserRepository;

import java.util.Optional;
import static java.lang.String.format;

@Component
public class RepositoryUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt
                .orElseThrow(
                        () -> new UsernameNotFoundException(format("User '%s' not found", username))
                );
    }
}
