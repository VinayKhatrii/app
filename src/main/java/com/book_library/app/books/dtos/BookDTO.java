package com.book_library.app.books.dtos;

import com.book_library.app.books.entities.BookEntity.BookStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDTO {
    private Long id;
    private String name;
    private String author;
    private BookCategoryDTO category;
    private BookStatus status;
    private Long borrowedById;
}
