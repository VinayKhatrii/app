package com.book_library.app.books.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.book_library.app.books.dtos.BookCategoryDTO;
import com.book_library.app.books.services.BookCategoryService;
import com.book_library.app.core.controller.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/book-categories")
@RequiredArgsConstructor
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookCategoryDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book Category Created Successfully", bookCategoryService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BookCategoryDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book Category Created Successfully", bookCategoryService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        bookCategoryService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Book Category deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book Category loaded successfully", bookCategoryService.getAll()));
    }
}
