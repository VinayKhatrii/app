package com.book_library.app.books.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book_library.app.books.dtos.BookCreateDTO;
import com.book_library.app.books.dtos.BookDTO;
import com.book_library.app.books.dtos.BookReportDTO;
import com.book_library.app.books.entities.BookEntity;
import com.book_library.app.books.entities.BorrowedBookEntity;
import com.book_library.app.books.entities.BookEntity.BookStatus;
import com.book_library.app.books.repositories.BookCategoryRepository;
import com.book_library.app.books.repositories.BookRepository;
import com.book_library.app.books.repositories.BorrowedBookRepository;
import com.book_library.app.user.entities.UserEntity;
import com.book_library.app.user.repositories.UserRepository;
import com.book_library.app.user.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BorrowedBookRepository borrowedBookRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final BookCategoryRepository bookCategoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(String name, String author, BookStatus status, Long categoryId,
            Pageable pageable) {
        Page<BookEntity> page = bookRepository.findByFilters(name, author, status, categoryId, pageable);
        return page.map(book -> modelMapper.map(book, BookDTO.class));
    }

    @Transactional(readOnly = true)
    public BookDTO getBookById(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));
        return modelMapper.map(book, BookDTO.class);
    }

    @Transactional
    public BookDTO borrowBook(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));

        if (!book.getStatus().equals(BookStatus.AVAILABLE)) {
            throw new IllegalArgumentException("Book is not available for borrowing");
        }
        
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + currentUsername));

        if (!currentUser.isAccountNonExpired()){
            throw new IllegalArgumentException("User account is expired and cannot borrow books");
        };

        book.setStatus(BookStatus.BORROWED);
        book.setBorrowedBy(currentUser);

        BookEntity updatedBook = bookRepository.save(book);

        BorrowedBookEntity borrowedBookEntity = new BorrowedBookEntity();
        borrowedBookEntity.setBook(updatedBook);
        borrowedBookEntity.setUser(currentUser);
        borrowedBookEntity.setBorrowedAt(java.time.LocalDateTime.now());
        borrowedBookEntity.setReturnedAt(null);
        borrowedBookRepository.save(borrowedBookEntity);

        return modelMapper.map(updatedBook, BookDTO.class);
    }

    @Transactional
    public BookDTO returnBook(Long bookId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));

        if (!book.getStatus().equals(BookStatus.BORROWED)) {
            throw new IllegalArgumentException("Book is not currently borrowed");
        }
        if (!book.getBorrowedBy().getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("Book is not borrowed by the current user");
        }

        book.setStatus(BookStatus.AVAILABLE);
        book.setBorrowedBy(null);

        BookEntity updatedBook = bookRepository.save(book);
        BorrowedBookEntity borrowedBookEntity = borrowedBookRepository.findByBookIdAndUserUsernameAndReturnedAtIsNull(bookId, currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Borrowed book record not found for book id: " + bookId));
        borrowedBookEntity.setReturnedAt(java.time.LocalDateTime.now());
        borrowedBookRepository.save(borrowedBookEntity);

        return modelMapper.map(updatedBook, BookDTO.class);
    }

    @Transactional
    public BookDTO createBook(BookCreateDTO bookDTO) {

        final BookEntity bookEntity = modelMapper.map(bookDTO, BookEntity.class);
        bookEntity.setId(null);
        if (bookDTO.getCategoryId() != null) {
            bookEntity.setCategory(bookCategoryRepository.findById(bookDTO.getCategoryId())
                    .orElseThrow(
                            () -> new IllegalArgumentException(
                                    "Book category not found with id: " + bookDTO.getCategoryId())));
        } else {
            bookEntity.setCategory(null);
        }

        BookEntity savedEntity = bookRepository.save(bookEntity);

        return modelMapper.map(savedEntity, BookDTO.class);
    }

    @Transactional
    public BookDTO createOrUpdateBook(BookDTO bookDTO) {

        final BookEntity bookEntity;

        if (bookDTO.getId() != null) {
            bookEntity = bookRepository.findById(bookDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookDTO.getId()));
        } else {
            bookEntity = modelMapper.map(bookDTO, BookEntity.class);
        }

        if (bookDTO.getCategory() != null) {
            bookEntity.setCategory(bookCategoryRepository.findById(bookDTO.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Book category not found with id: " + bookDTO.getCategory().getId())));
        } else {
            bookEntity.setCategory(null);
        }

        if (bookDTO.getBorrowedById() != null) {
            bookEntity.setBorrowedBy(userRepository.findById(bookDTO.getBorrowedById())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + bookDTO.getBorrowedById())));
        } else {
            bookEntity.setBorrowedBy(null);
        }

        BookEntity savedEntity = bookRepository.save(bookEntity);

        return modelMapper.map(savedEntity, BookDTO.class);
    }

    @Transactional(readOnly = true)
    public List<BookReportDTO> getBookReports() {
        List<BookEntity> books = bookRepository.findAll();
        long totalReads = books.stream()
                .mapToLong(book -> borrowedBookRepository.countByBookId(book.getId()))
                .sum();

        List<BookReportDTO> reports = books.stream()
                .map(book -> {
                    long timesRead = borrowedBookRepository.countByBookId(book.getId());
                    int percentage = totalReads > 0 ? (int) ((timesRead * 100) / totalReads) : 0;
                    return new BookReportDTO(modelMapper.map(book, BookDTO.class), timesRead, percentage);
                })
                .toList();
        return reports;
    }

    @Transactional
    public void deleteBook(Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        bookRepository.delete(book);
    }
}
