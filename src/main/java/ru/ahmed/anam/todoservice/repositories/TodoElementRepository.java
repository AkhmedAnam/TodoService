package ru.ahmed.anam.todoservice.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.ahmed.anam.todoservice.domain.TodoElement;
import ru.ahmed.anam.todoservice.domain.TodoList;

public interface TodoElementRepository extends CrudRepository<TodoElement, Long> {
    Iterable<TodoElement> findAllByList(TodoList todoList);
}
