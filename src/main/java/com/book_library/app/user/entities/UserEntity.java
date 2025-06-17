package com.book_library.app.user.entities;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.book_library.app.books.entities.BookEntity;
import com.book_library.app.books.entities.BorrowedBookEntity;
import com.book_library.app.core.entities.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends BaseEntity implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    private String firstName;
    private String lastName;

    private LocalDate membershipEndDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BorrowedBookEntity> borrowedBooks;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return membershipEndDate == null || LocalDate.now().isBefore(membershipEndDate);
    }

}
