package ru.ahmed.anam.todoservice.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "TodoElement")
public class TodoElement implements Serializable {

    private static final long serialVersionUID = -872468606805618875L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "listId", nullable = false)
    private TodoList list;
    private String text;
    private boolean done;

    /**
     * Меняет признак "Выполнено" на прротивоположенный. Чтобы иметь возможность удобно помечать
     * выполненные элементы как НЕвыполненные и наоборот
     */
    public TodoElement done(){
        done = !done;
        return this;
    }

}
