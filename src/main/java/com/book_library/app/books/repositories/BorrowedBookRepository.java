package com.book_library.app.books.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.book_library.app.books.entities.BorrowedBookEntity;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBookEntity, Long>, JpaSpecificationExecutor<BorrowedBookEntity>{
    Optional<BorrowedBookEntity> findByBookIdAndUserUsernameAndReturnedAtIsNull(Long bookId, String username);

    long countByBookId(Long bookId);
}
