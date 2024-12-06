package com.bookrental.api.rent;

import com.bookrental.api.rent.request.GetRentParams;
import com.bookrental.api.rent.response.Rent;
import com.bookrental.config.exceptions.ErrorResponse;
import com.bookrental.config.exceptions.ForbiddenException;
import com.bookrental.security.SecurityUtil;
import com.bookrental.service.rent.RentDto;
import com.bookrental.service.rent.RentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/rents")
@Tag(name = "Rent API", description = "API for managing book rentals")
public class RentController {

    private final RentService rentService;
    private final RentMapper rentMapper;
    private final SecurityUtil securityUtil;

    @Autowired
    public RentController(RentService rentService, RentMapper rentMapper, SecurityUtil securityUtil) {
        this.rentService = rentService;
        this.rentMapper = rentMapper;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/book/{bookPublicId}/user/{userPublicId}")
    @Operation(summary = "Create a new rent", description = "Creates a new rent for a book and user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rent created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rent.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or book not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<Rent> createRent(@PathVariable String bookPublicId, @PathVariable String userPublicId) {
        RentDto rentDto = rentService.createRent(bookPublicId, userPublicId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentMapper.mapRentDtoToRent(rentDto));
    }

    @GetMapping("/user/{userPublicId}")
    @Operation(summary = "Get list of user rents", description = "Retrieves a paginated list of rents for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user rents retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rent.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public List<Rent> getUserRents(@PathVariable String userPublicId, GetRentParams params) throws ForbiddenException {
        securityUtil.checkUserAccess(userPublicId);
        List<RentDto> rents = rentService.getRents(userPublicId, params);
        return rents.stream()
                .map(rentMapper::mapRentDtoToRent)
                .toList();
    }

    @PatchMapping("/end/{rentPublicId}")
    @Operation(summary = "End a rent", description = "Updates the end date of a rent by its public ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rent ended successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rent.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rent not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public Rent endRent(@PathVariable String rentPublicId) {
        RentDto rent = rentService.endRent(rentPublicId);
        return rentMapper.mapRentDtoToRent(rent);
    }

}
