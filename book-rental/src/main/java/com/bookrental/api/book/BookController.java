package com.bookrental.api.book;

import com.bookrental.api.book.request.CreateBookRequest;
import com.bookrental.api.book.response.Book;
import com.bookrental.config.exceptions.ErrorResponse;
import com.bookrental.service.book.BookDto;
import com.bookrental.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/books")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    @PostMapping("/add")
    @Operation(
            summary = "Add a new book",
            description = "Creates a new book by providing its title, ISBN, publication year, publisher, and authors."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Book> addBook(@RequestBody CreateBookRequest bookRequest) {
        BookDto book = bookService.addBook(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookMapper.mapBookDtoToBook(book));
    }

}
