package com.github.jvanheesch.adapter;

import org.springframework.stereotype.Component;

@Component
public class UserServiceClient {
    public UserDTO getUser(String username) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(username);
        return userDTO;
    }
}
