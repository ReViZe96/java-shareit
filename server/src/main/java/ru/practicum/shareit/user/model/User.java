package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", length = 512, nullable = false)
    private String email;

}
