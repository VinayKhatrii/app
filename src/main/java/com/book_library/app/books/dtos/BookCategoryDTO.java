package com.book_library.app.books.dtos;

import com.book_library.app.core.dtos.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookCategoryDTO extends BaseDTO{
    private Long id;
    private String name;
    private String description;
}
