package com.bookrental.api.book;

import com.bookrental.api.author.response.Author;
import com.bookrental.api.book.response.Book;
import com.bookrental.service.book.BookDto;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book mapBookDtoToBook(BookDto book) {
        return Book.builder()
                .publicId(book.getPublicId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .amount(book.getAmount())
                .publicationYear(book.getPublicationYear())
                .authors(book.getAuthors().stream()
                        .map(authorDto -> Author.builder()
                                .name(authorDto.getName())
                                .birthYear(authorDto.getBirthYear())
                                .publicId(authorDto.getPublicId())
                                .build())
                        .toList())
                .build();
    }

}
