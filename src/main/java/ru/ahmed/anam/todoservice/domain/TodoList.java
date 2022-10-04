package ru.ahmed.anam.todoservice.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "TodoList")
public class TodoList implements Serializable {

    private static final long serialVersionUID = -8666901598381966256L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    private String title;
    @OneToMany
    private List<TodoElement> elements;
}
