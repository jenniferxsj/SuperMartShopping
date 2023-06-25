package com.example.superdupermart.dto.user;

import com.example.superdupermart.domain.User;
import com.example.superdupermart.dto.common.ServiceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AllUserResponse {
    ServiceStatus serviceStatus;
    List<User> users;
}
