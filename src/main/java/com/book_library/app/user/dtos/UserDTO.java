package com.book_library.app.user.dtos;

import java.util.List;

import com.book_library.app.books.dtos.BookDTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private List<BookDTO> borrowedBooks;

}
