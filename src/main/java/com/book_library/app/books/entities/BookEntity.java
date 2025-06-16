package com.book_library.app.books.entities;

import com.book_library.app.core.entities.BaseEntity;
import com.book_library.app.user.entities.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "books")
@Data
@EqualsAndHashCode(callSuper = true)
public class BookEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private BookCategoryEntity category;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @ManyToOne
    @JoinColumn(name = "borrowed_by")
    private UserEntity borrowedBy;

    @Getter
    @RequiredArgsConstructor
    public enum BookStatus {
        AVAILABLE("Available"),
        CHECKED_OUT("Checked Out"),
        RESERVED("Reserved");

        private final String displayName;
    }
}
