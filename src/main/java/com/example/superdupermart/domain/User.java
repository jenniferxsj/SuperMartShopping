package com.example.superdupermart.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity //want to map it to the database table
@Table(name="user")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="email", unique = true, nullable = false)
    @NotEmpty(message = "Please provide your email")
    private String email;

    @Column(name = "password", length = 60)
    @Length(min = 6, message = "Your password must have at least 5 characters")
    @NotEmpty(message = "Please provide your password")
    private String passwordHash;

    @Column(name = "role")
    private int role;

    @Column(name = "username", nullable = false, length = 30, unique = true)
    @NotEmpty(message = "Please provide your User Name")
    private String username;
}
