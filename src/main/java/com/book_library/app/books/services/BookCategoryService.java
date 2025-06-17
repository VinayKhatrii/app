package com.book_library.app.books.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.book_library.app.books.dtos.BookCategoryDTO;
import com.book_library.app.books.entities.BookCategoryEntity;
import com.book_library.app.books.repositories.BookCategoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;
    private final ModelMapper modelMapper;

    public BookCategoryDTO create(BookCategoryDTO dto) {
        BookCategoryEntity entity = modelMapper.map(dto, BookCategoryEntity.class);
        BookCategoryEntity saved = bookCategoryRepository.save(entity);
        return modelMapper.map(saved, BookCategoryDTO.class);
    }

    public BookCategoryDTO update(Long id, BookCategoryDTO dto) {
        BookCategoryEntity existing = bookCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BookCategory not found with id: " + id));
        existing.setName(dto.getName());
        BookCategoryEntity updated = bookCategoryRepository.save(existing);
        return modelMapper.map(updated, BookCategoryDTO.class);
    }

    public void delete(Long id) {
        if (!bookCategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("BookCategory not found with id: " + id);
        }
        bookCategoryRepository.deleteById(id);
    }

    public List<BookCategoryDTO> getAll() {
        return bookCategoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, BookCategoryDTO.class))
                .collect(Collectors.toList());
    }
}
