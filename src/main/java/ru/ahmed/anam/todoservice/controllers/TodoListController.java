package ru.ahmed.anam.todoservice.controllers;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.ahmed.anam.todoservice.domain.TodoElement;
import ru.ahmed.anam.todoservice.domain.TodoList;
import ru.ahmed.anam.todoservice.domain.User;
import ru.ahmed.anam.todoservice.repositories.TodoElementRepository;
import ru.ahmed.anam.todoservice.repositories.TodoListRepository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.lang.String.format;

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
    public Iterable<TodoList> getAllLists() {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return listRepository.findAllByUserUsername(user.getUsername());
    }

    @GetMapping("/{listId}")
    public ResponseEntity<TodoList> getListById(@PathVariable("listId") Long listId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Optional<TodoList> optResult = listRepository.findById(listId);
        if (optResult.isPresent()) {
            final TodoList todoList = optResult.get();
            final User listOwner = todoList.getUser();
            if (currentUser.equals(listOwner)) {
                //возвращаем туду-лист только если он принадлежит текущему пользователю
                return new ResponseEntity<>(todoList, HttpStatus.OK);
            } else {
                log.log(
                        Level.WARNING,
                        format(
                                "Пользователь '%s' пытался получить список (listId=%d), принадлежащий другому другому пользователю - '%s'",
                                currentUser.getUsername(), listId, listOwner.getUsername()
                        )
                );
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoList postTodoList(@RequestBody TodoList todoList) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        todoList.setUser(user);
        List<TodoElement> todoListElements = todoList.getElements();
        todoListElements = todoListElements.stream()
                .map(elementRepository::save)
                .collect(Collectors.toList());
        todoList.setElements(todoListElements);
        return listRepository.save(todoList);
    }

    @DeleteMapping(path = "/{listId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodoList(@PathVariable("listId") Long listId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Optional<TodoList> list = listRepository.findById(listId);
        if (list.isEmpty()) {
            log.log(Level.WARNING, format("Список {id=%d} не найден", listId));
            return;
        }
        final TodoList todoList = list.get();
        final User listUser = todoList.getUser();
        if (!listUser.equals(currentUser)) {
            log.log(
                    Level.WARNING,
                    format(
                            "Пользователь '%s' пытался удалить список {id=%d} (принадлежащий пользователю '%s')",
                            currentUser.getUsername(),
                            listId,
                            listUser.getUsername()
                    )
            );
            return;
        }
        try {
            listRepository.deleteById(listId);
        } catch (EmptyResultDataAccessException e) {
            log.log(Level.WARNING, String.format("Error occured during deleting a todo-list with ID={%d}", listId), e);
        }
    }

    @PatchMapping(path = "/element/{elementId}/done")
    public ResponseEntity<TodoElement> markElementAsDone(@PathVariable("elementId") Long elementId) {
        final User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Optional<TodoElement> optTodoElement = elementRepository.findById(elementId);
        if (optTodoElement.isPresent()) {
            final TodoElement todoElement = optTodoElement.get();
            final TodoList todoList = listRepository.findByElementsContains(todoElement);
            final User listOwner = todoList.getUser();
            if (listOwner.equals(currentUser)) {
                //помечаем элемент списка только если лист, в состав которого он входит, принадлежит текущему пользователю
                final TodoElement updatedTodoElement = todoElement.done();
                elementRepository.save(updatedTodoElement);
                return new ResponseEntity<>(updatedTodoElement, HttpStatus.OK);
            } else {
                log.log(
                        Level.WARNING,
                        format(
                                "Пользователь '%s' пытался изменить элемент списка, принадлежащий другому другому пользователю - '%s'",
                                currentUser.getUsername(), listOwner.getUsername()
                                )
                );
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


}
