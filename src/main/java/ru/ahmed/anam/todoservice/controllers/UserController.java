package ru.ahmed.anam.todoservice.controllers;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ahmed.anam.todoservice.domain.User;
import ru.ahmed.anam.todoservice.repositories.UserRepository;

import java.util.logging.Level;

@Log
@RestController
@CrossOrigin
@RequestMapping(path = "/users", produces = "application/json")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<User> registerNewUser(@RequestBody User userToRegister) {
        final String username = userToRegister.getUsername();
        return userRepository.findByUsername(username)
                .map(
                        user -> {
                            log.log(Level.WARNING, String.format("User '%s' already exist", username));
                            return new ResponseEntity<>(userToRegister, HttpStatus.CONFLICT);
                        }
                )
                .orElseGet(
                        () -> new ResponseEntity<>(userRepository.save(userToRegister), HttpStatus.OK)
                );
    }
}
