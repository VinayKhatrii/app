package com.book_library.app.books.dtos;

import com.book_library.app.books.entities.BookEntity.BookStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private Long borrowedById;
}
