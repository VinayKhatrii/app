package com.book_library.app.books.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book_library.app.books.dtos.BookCreateDTO;
import com.book_library.app.books.dtos.BookDTO;
import com.book_library.app.books.entities.BookEntity.BookStatus;
import com.book_library.app.books.services.BookService;
import com.book_library.app.core.controller.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    
    @GetMapping
    public ResponseEntity<?> getAllBooks(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) BookStatus status,
            @RequestParam(required = false) Long categoryId) {
        Page<BookDTO> books = bookService.getAllBooks(name, author, status, categoryId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Books Loaded Successfully", books));
    }

    @GetMapping("/{bookId}/borrow")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book Borrowed Successfully", bookService.borrowBook(bookId)));
    }

    @GetMapping("/{bookId}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book Returned Successfully", bookService.returnBook(bookId)));
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getBookReports() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Books Report Loaded Successfully",  bookService.getBookReports()));
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookCreateDTO bookDTO) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book Created Successfully", bookService.createBook(bookDTO)));
    }

}
