package ru.ahmed.anam.todoservice.controllers;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.ahmed.anam.todoservice.domain.User;
import ru.ahmed.anam.todoservice.repositories.UserRepository;

import java.util.Optional;
import java.util.logging.Level;

import static java.lang.String.format;

@Log
@RestController
@CrossOrigin
@RequestMapping(path = "/users", produces = "application/json")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<User> registerNewUser(@RequestBody User user) {
        /**
         * Если мы используем Http Basic аутентификацию, то при каждом вызове endpoint-а в запросе уже должна быть
         * информацию о юзере (username, password). Т.к. наш класс User состоит из этих двух полей, то незачем в
         * запросе передовать json с описаниием юзера
         */
        final String username = user.getUsername();
        final String password = user.getPassword();
        final Optional<User> optUser = userRepository.findByUsername(username);
        if(optUser.isPresent()){
            log.log(Level.WARNING, format("User '%s' already exist", username));
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } else {
            user.setPassword(passwordEncoder.encode(password));
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        }
    }
}
