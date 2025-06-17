package com.book_library.app.user.dtos;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
