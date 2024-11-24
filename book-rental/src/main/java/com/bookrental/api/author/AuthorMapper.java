package com.bookrental.api.author;

import com.bookrental.api.author.response.Author;
import com.bookrental.service.author.AuthorDto;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author mapAuthorDtoToAuthor(AuthorDto author) {
        return Author.builder()
                .name(author.getName())
                .publicId(author.getPublicId())
                .birthYear(author.getBirthYear())
                .build();
    }

}
