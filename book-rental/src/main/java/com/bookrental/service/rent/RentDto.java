package com.bookrental.service.rent;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentDto {

    String publicId;
    String userPublicId;
    String bookPublicId;
    String bookTitle;
    List<String> bookAuthors;
    LocalDateTime startDate;
    LocalDateTime endDate;

}