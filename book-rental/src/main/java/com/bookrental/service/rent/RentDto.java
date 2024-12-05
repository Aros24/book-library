package com.bookrental.service.rent;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentDto {

    String publicId;
    String userPublicId;
    String bookPublicId;
    LocalDateTime startDate;
    LocalDateTime endDate;

}