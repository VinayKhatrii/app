package com.book_library.app.books.type_maps;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import com.book_library.app.books.dtos.BookDTO;
import com.book_library.app.books.entities.BookEntity;
import com.book_library.app.user.entities.UserEntity;

public class BookTypeMap {
    public static void configure(ModelMapper modelMapper) {
        fromEntityToDTO(modelMapper);
    }

    private static void fromEntityToDTO(ModelMapper modelMapper) {
        TypeMap<BookEntity, BookDTO> typeMap = modelMapper.createTypeMap(BookEntity.class, BookDTO.class);
        typeMap.addMappings(mapper -> {
            mapper.using(ctx -> {
                var category = (UserEntity) ctx.getSource();
                return category != null ? category.getId() : null;
            }).map(BookEntity::getBorrowedBy, BookDTO::setBorrowedById);
        });
    }
}
