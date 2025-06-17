package com.book_library.app.books.dtos;

public record BookReportDTO(BookDTO book, Long timesRead, int percentage) {
    
}
