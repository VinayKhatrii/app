package com.book_library.app.user.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.book_library.app.user.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    public Optional<UserEntity> findByUsername(String username);
    
    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);
}
