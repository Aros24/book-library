package com.bookrental.api.author;

import com.bookrental.api.author.request.CreateAuthorRequest;
import com.bookrental.api.author.request.GetAuthorParams;
import com.bookrental.api.author.response.Author;
import com.bookrental.config.exceptions.ErrorResponse;
import com.bookrental.service.author.AuthorDto;
import com.bookrental.service.author.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Author", description = "Author related endpoints")
@RestController
@RequestMapping("/v1/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @PostMapping
    @Operation(
            summary = "Create a new author",
            description = "Creates a new author with the provided name and birth year."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Author created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Author createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        AuthorDto authorDto = authorService.createAuthor(request);
        return authorMapper.mapAuthorDtoToAuthor(authorDto);
    }

    @GetMapping
    @Operation(summary = "Get authors", description = "Retrieve a paginated list of authors, optionally filtered by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authors retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class))),
            @ApiResponse(responseCode = "404", description = "No authors found", content = @Content)
    })
    public List<Author> getAuthors(GetAuthorParams params) {
        List<AuthorDto> authors = authorService.getAuthors(params);
        return authors.stream()
                .map(authorMapper::mapAuthorDtoToAuthor)
                .toList();
    }

}
