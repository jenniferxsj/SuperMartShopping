package com.example.superdupermart.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationRequest {
    String username;
    String password;
    String email;
    int role;
}
