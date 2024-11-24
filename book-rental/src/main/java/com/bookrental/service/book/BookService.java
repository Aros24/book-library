package com.bookrental.service.book;

import com.bookrental.api.book.request.CreateBookRequest;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.entity.Book;
import com.bookrental.persistence.repositories.AuthorRepository;
import com.bookrental.persistence.repositories.BookRepository;
import com.bookrental.service.ServiceUtil;
import com.bookrental.service.author.AuthorDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ServiceUtil serviceUtil;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       ServiceUtil serviceUtil) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.serviceUtil = serviceUtil;
    }

    @Transactional
    public BookDto addBook(CreateBookRequest bookRequest) {
        List<Author> authors = authorRepository.findByPublicIdIn(bookRequest.getAuthorPublicIds());
        if (authors.size() != bookRequest.getAuthorPublicIds().size()) {
            throw new BadRequestException("Some authors were not found for the provided public IDs.");
        }

        Book book = Book.builder()
                .publicId(serviceUtil.generateRandomUUID())
                .title(bookRequest.getTitle())
                .isbn(bookRequest.getIsbn())
                .publicationYear(bookRequest.getPublicationYear())
                .publisher(bookRequest.getPublisher())
                .authors(authors)
                .build();
        book = bookRepository.save(book);

        return buildBook(book);
    }

    private BookDto buildBook(Book book) {
        return BookDto.builder()
                .publicId(book.getPublicId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .publisher(book.getPublisher())
                .authors(book.getAuthors().stream()
                        .map(author -> AuthorDto.builder()
                                .publicId(author.getPublicId())
                                .name(author.getName())
                                .birthYear(author.getBirthYear())
                                .build())
                        .toList())
                .build();
    }

}
