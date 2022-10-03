package ru.ahmed.anam.todoservice.controllers;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.ahmed.anam.todoservice.domain.TodoElement;
import ru.ahmed.anam.todoservice.domain.TodoList;
import ru.ahmed.anam.todoservice.domain.User;
import ru.ahmed.anam.todoservice.repositories.TodoElementRepository;
import ru.ahmed.anam.todoservice.repositories.TodoListRepository;

import java.util.Optional;
import java.util.logging.Level;

@Log
@RestController
@CrossOrigin
@RequestMapping(path = "/lists", produces = "application/json")
public class TodoListController {

    private final TodoListRepository listRepository;
    private final TodoElementRepository elementRepository;

    @Autowired
    public TodoListController(TodoListRepository listRepository, TodoElementRepository elementRepository) {
        this.listRepository = listRepository;
        this.elementRepository = elementRepository;
    }

    @GetMapping
    public Iterable<TodoList> getAllLists(){
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return listRepository.findAllByUserUsername(user.getUsername());
    }

    @GetMapping("/{listId}")
    public ResponseEntity<TodoList> getListById(@PathVariable("listId") Long listId){
        final Optional<TodoList> optResult = listRepository.findById(listId);
        return optResult
                .map(
                        todoList -> new ResponseEntity<>(todoList, HttpStatus.OK)
                )
                .orElseGet(
                        () -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND)
                );
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoList postTodoList(@RequestBody TodoList todoList){
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        todoList.setUser(user);
        return listRepository.save(todoList);
    }

    @DeleteMapping(path = "/{listId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoList(@PathVariable("listId") Long listId){
        try {
            listRepository.deleteById(listId);
        } catch (EmptyResultDataAccessException e){
            log.log(Level.WARNING, String.format("Error occured during deleting a todo-list with ID={%d}", listId), e);
        }
    }

    @PatchMapping(path = "/element/{elementId}/done")
    public ResponseEntity<TodoElement> markElementAsDone(@PathVariable("elementId") Long elementId){
        final Optional<TodoElement> optTodoElement = elementRepository.findById(elementId);
        if(optTodoElement.isPresent()){
            final TodoElement updatedTodoElement = optTodoElement.get().done();
            return new ResponseEntity<>(updatedTodoElement, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


}
