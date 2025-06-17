package com.book_library.app.books.dtos;

import com.book_library.app.books.entities.BookEntity.BookStatus;

import lombok.Data;

@Data
public class BookCreateDTO {
    private String name;
    private String author;
    private Long categoryId;
    private BookStatus status;
}
