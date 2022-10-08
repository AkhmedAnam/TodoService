package ru.ahmed.anam.todoservice.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.ahmed.anam.todoservice.domain.TodoElement;
import ru.ahmed.anam.todoservice.domain.TodoList;
import ru.ahmed.anam.todoservice.domain.User;

public interface TodoListRepository extends CrudRepository<TodoList, Long> {
    Iterable<TodoList> findAllByUserUsername(String username);

    TodoList findByElementsContains(TodoElement todoElement);
}
