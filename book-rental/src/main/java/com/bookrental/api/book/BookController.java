package com.bookrental.api.book;

import com.bookrental.api.book.request.CreateBookRequest;
import com.bookrental.api.book.request.GetBookParams;
import com.bookrental.api.book.response.Book;
import com.bookrental.config.exceptions.ErrorResponse;
import com.bookrental.service.book.BookDto;
import com.bookrental.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Book", description = "Books related endpoints")
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

    @GetMapping("/{publicId}")
    @Operation(summary = "Get book by public ID", description = "Fetches a book by its public ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Book> getBookByPublicId(@PathVariable String publicId) {
        BookDto book = bookService.getBookByPublicId(publicId);
        return ResponseEntity.ok(bookMapper.mapBookDtoToBook(book));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve a list of books with optional filters",
            description = "Fetch books filtered by publisher, publication year, title, or author name. Supports pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Book.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No books found matching the filters",
                    content = @Content(mediaType = "application/json"))
    })
    public List<Book> getBooks(GetBookParams request) {
        List<BookDto> books = bookService.getBooks(request);
        return books.stream()
                .map(bookMapper::mapBookDtoToBook)
                .toList();
    }

    @PatchMapping("/{publicId}/amount")
    @Operation(summary = "Change book amount", description = "Changes the amount of a specific book by its public ID. Can be increased or decreased by providing a positive or negative value.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book amount changed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "Book not found or amount is incorrect",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Book changeBookAmount(@PathVariable String publicId, @RequestParam("value") int amount) {
        BookDto book = bookService.changeBookAmount(publicId, amount);
        return bookMapper.mapBookDtoToBook(book);
    }

}
