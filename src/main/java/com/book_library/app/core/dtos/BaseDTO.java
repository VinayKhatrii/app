package com.book_library.app.core.dtos;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseDTO {
    @JsonIgnore
    private Timestamp createdDate;
    @JsonIgnore
    private Timestamp updatedDate;
}
