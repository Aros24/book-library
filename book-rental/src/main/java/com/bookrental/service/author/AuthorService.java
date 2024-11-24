package com.bookrental.service.author;

import com.bookrental.api.author.request.CreateAuthorRequest;
import com.bookrental.api.author.request.GetAuthorParams;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.repositories.AuthorRepository;
import com.bookrental.service.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final ServiceUtil serviceUtil;
    private final PersistenceUtil persistenceUtil;

    private final String NAME = "name";
    private final String ORDER_BY = "asc";

    @Autowired
    public AuthorService(AuthorRepository authorRepository, ServiceUtil serviceUtil,
                         PersistenceUtil persistenceUtil) {
        this.authorRepository = authorRepository;
        this.serviceUtil = serviceUtil;
        this.persistenceUtil = persistenceUtil;
    }

    public AuthorDto createAuthor(CreateAuthorRequest request) {
        Author newAuthor = Author.builder()
                .publicId(serviceUtil.generateRandomUUID())
                .name(request.getName())
                .birthYear(request.getBirthYear())
                .build();

        authorRepository.save(newAuthor);
        return buildAuthor(newAuthor);
    }

    public List<AuthorDto> getAuthors(GetAuthorParams params) {
        Pageable pageable = persistenceUtil.buildPageable(params.getSize(), params.getPage(), NAME, ORDER_BY);
        Specification<Author> specification = persistenceUtil.buildAuthorListSpecification(params.getName());

        List<AuthorDto> authors = authorRepository.findAll(specification, pageable).stream()
                .map(this::buildAuthor)
                .toList();

        if (authors.isEmpty()) {
            throw new ResourceNotFoundException("No authors found.");
        }

        return authors;
    }

    private AuthorDto buildAuthor(Author author) {
        return AuthorDto.builder()
                .publicId(author.getPublicId())
                .name(author.getName())
                .birthYear(author.getBirthYear())
                .build();
    }

}
