package com.book_library.app.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.book_library.app.books.entities.BookCategoryEntity;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategoryEntity, Long>, JpaSpecificationExecutor<BookCategoryEntity> {
  
}
