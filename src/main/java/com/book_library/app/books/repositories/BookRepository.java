package com.book_library.app.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.book_library.app.books.entities.BookEntity;
import com.book_library.app.books.entities.BookEntity.BookStatus;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {
    @Query("SELECT b FROM BookEntity b " +
            "WHERE (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND (:categoryId IS NULL OR b.category.id = :categoryId)")
    Page<BookEntity> findByFilters(
            @Param("name") String name,
            @Param("author") String author,
            @Param("status") BookStatus status,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

}
