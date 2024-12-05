package com.bookrental.service.rent;

import com.bookrental.api.rent.request.GetRentParams;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.RentDbAccessor;
import com.bookrental.persistence.constants.RentConstants;
import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.entity.Book;
import com.bookrental.persistence.entity.Rent;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.RentRepository;
import com.bookrental.service.book.BookService;
import com.bookrental.service.user.UserService;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class RentService {

    private final UserService userService;
    private final BookService bookService;
    private final RentRepository rentRepository;
    private final PersistenceUtil persistenceUtil;
    private final RentDbAccessor rentDbAccessor;
    private static final int INCREASE_BY_ONE = 1;

    @Autowired
    public RentService(UserService userService, BookService bookService,
                       RentRepository rentRepository, PersistenceUtil persistenceUtil,
                       RentDbAccessor rentDbAccessor) {
        this.userService = userService;
        this.bookService = bookService;
        this.rentRepository = rentRepository;
        this.persistenceUtil = persistenceUtil;
        this.rentDbAccessor = rentDbAccessor;
    }

    public RentDto createRent(String bookPublicId, String userPublicId) {
        User user = userService.getUserRawEntityByPublicId(userPublicId);
        Book book = bookService.getBookRawEntityByPublicId(bookPublicId);

        try {
            return buildRent(rentDbAccessor.finishRentCreation(user, book));
        } catch (Exception e) {
            if (e.getMessage().contains("Check constraint")) {
                throw new BadRequestException("Book cannot be rented!");
            }
            throw e;
        }
    }

    public List<RentDto> getRents(String userPublicId, GetRentParams params) {
        Pageable pageable = persistenceUtil.buildPageable(params.getSize(), params.getPage(),
                RentConstants.START_DATE.getValue(), Sort.Direction.DESC.toString());
        Specification<Rent> specification = persistenceUtil.buildRentListSpecification(userPublicId);

        List<RentDto> rentDtoList = rentRepository.findAll(specification, pageable).stream()
                .map(this::buildRent)
                .toList();

        if (rentDtoList.isEmpty()) {
            throw new ResourceNotFoundException("Rents not found for the provided criteria");
        }

        return rentDtoList;
    }

    @Transactional
    public RentDto endRent(String rentPublicId) {
        Rent rent = rentRepository.findByPublicId(rentPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Rent not found"));

        rent.setEndDate(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        rentRepository.save(rent);
        bookService.changeBookAmount(rent.getBookPublicId(), INCREASE_BY_ONE);

        return buildRent(rent);
    }

    private RentDto buildRent(Rent rent) {
        return RentDto.builder()
                .publicId(rent.getPublicId())
                .userPublicId(rent.getUserPublicId())
                .bookPublicId(rent.getBookPublicId())
                .bookTitle(rent.getBook().getTitle())
                .bookAuthors(rent.getBook().getAuthors().stream()
                        .map(Author::getName)
                        .toList())
                .startDate(rent.getStartDate())
                .endDate(rent.getEndDate())
                .build();
    }

}