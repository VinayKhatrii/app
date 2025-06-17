package com.book_library.app.books.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BorrowedBookDTO {
    private Long id;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    private Long bookId;
    private Long userId;

}
