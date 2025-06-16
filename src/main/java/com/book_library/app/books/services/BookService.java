package com.book_library.app.books.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book_library.app.books.dtos.BookDTO;
import com.book_library.app.books.entities.BookEntity;
import com.book_library.app.books.repositories.BookCategoryRepository;
import com.book_library.app.books.repositories.BookRepository;
import com.book_library.app.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final BookCategoryRepository bookCategoryRepository;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public BookDTO getBookById(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        return modelMapper.map(book, BookDTO.class);
    }

    @Transactional
    public BookDTO createOrUpdateBook(BookDTO bookDTO) {

        final BookEntity bookEntity;

        if(bookDTO.getId() != null) {
            bookEntity = bookRepository.findById(bookDTO.getId())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookDTO.getId()));
        } else {
            bookEntity = modelMapper.map(bookDTO, BookEntity.class);
        }

        if(bookDTO.getCategory() != null) {
            bookEntity.setCategory(bookCategoryRepository.findById(bookDTO.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Book category not found with id: " + bookDTO.getCategory().getId())));
        } else {
            bookEntity.setCategory(null);
        }

        if(bookDTO.getBorrowedById() != null){
            bookEntity.setBorrowedBy(userRepository.findById(bookDTO.getBorrowedById())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + bookDTO.getBorrowedById())));
        } else {
            bookEntity.setBorrowedBy(null);
        }

        BookEntity savedEntity = bookRepository.save(bookEntity);
        
        return modelMapper.map(savedEntity, BookDTO.class);
    }
    
    @Transactional
    public void deleteBook(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        bookRepository.delete(book);
    }
}
