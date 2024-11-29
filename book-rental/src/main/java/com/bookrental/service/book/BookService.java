package com.bookrental.service.book;

import com.bookrental.api.book.request.CreateBookRequest;
import com.bookrental.api.book.request.GetBookParams;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.entity.Book;
import com.bookrental.persistence.repositories.AuthorRepository;
import com.bookrental.persistence.repositories.BookRepository;
import com.bookrental.service.ServiceUtil;
import com.bookrental.service.author.AuthorDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hibernate.type.descriptor.java.IntegerJavaType.ZERO;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ServiceUtil serviceUtil;
    private final PersistenceUtil persistenceUtil;
    private static final int AMOUNT_ON_CREATE = 1;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       ServiceUtil serviceUtil, PersistenceUtil persistenceUtil) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.serviceUtil = serviceUtil;
        this.persistenceUtil = persistenceUtil;
    }

    @Transactional
    public BookDto addBook(CreateBookRequest bookRequest) {
        List<Author> authors = authorRepository.findByPublicIdIn(bookRequest.getAuthorPublicIds());
        if (authors.size() != bookRequest.getAuthorPublicIds().size()) {
            throw new BadRequestException("Some authors were not found for the provided public IDs.");
        }
        Book bookByIsbn = bookRepository.findBookByIsbn(bookRequest.getIsbn());
        if (bookByIsbn != null) {
            throw new BadRequestException("Book with the provided ISBN already exists.");
        }

        Book book = Book.builder()
                .publicId(serviceUtil.generateRandomUUID())
                .title(bookRequest.getTitle())
                .isbn(bookRequest.getIsbn())
                .amount(AMOUNT_ON_CREATE)
                .publicationYear(bookRequest.getPublicationYear())
                .publisher(bookRequest.getPublisher())
                .authors(authors)
                .build();
        book = bookRepository.save(book);

        return buildBook(book);
    }

    public BookDto getBookByPublicId(String publicId) {
        Book book = bookRepository.findBookByPublicId(publicId);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }
        return buildBook(book);
    }

    public List<BookDto> getBooks(GetBookParams params) {
        Pageable pageable = persistenceUtil.buildPageable(params.getSize(), params.getPage(), params.getOrderBy(), params.getOrderDirection());
        Specification<Book> specification = persistenceUtil.buildBookListSpecification(params);

        List<BookDto> booksDtoList = bookRepository.findAll(specification, pageable).stream()
                .map(this::buildBook)
                .toList();

        if (booksDtoList.isEmpty()) {
            throw new BadRequestException("No books found for the provided criteria.");
        }

        return booksDtoList;
    }

    @Transactional
    public BookDto changeBookAmount(String publicId, int amount) {
        int updatedRows = bookRepository.changeBookAmount(publicId, amount);
        if (updatedRows == ZERO) {
            throw new ResourceNotFoundException("Book not found or amount is incorrect");
        }
        Book book = bookRepository.findBookByPublicId(publicId);
        return buildBook(book);
    }

    private BookDto buildBook(Book book) {
        return BookDto.builder()
                .publicId(book.getPublicId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .amount(book.getAmount())
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
