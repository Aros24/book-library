package com.bookrental.api.rent;

import com.bookrental.api.rent.response.Rent;
import com.bookrental.service.rent.RentDto;
import org.springframework.stereotype.Component;

@Component
public class RentMapper {

    public Rent mapRentDtoToRent(RentDto rentDto) {
        return Rent.builder()
                .publicId(rentDto.getPublicId())
                .userPublicId(rentDto.getUserPublicId())
                .bookPublicId(rentDto.getBookPublicId())
                .bookTitle(rentDto.getBookTitle())
                .bookAuthors(rentDto.getBookAuthors())
                .startDate(rentDto.getStartDate())
                .endDate(rentDto.getEndDate())
                .build();
    }

}
