package com.book_library.app.core.configs;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.book_library.app.books.type_maps.BookTypeMap;


@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        
        BookTypeMap.configure(modelMapper);

        return modelMapper;
    }

}
