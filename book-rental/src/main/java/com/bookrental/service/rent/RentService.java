package com.bookrental.service.rent;

import com.bookrental.api.rent.request.GetRentParams;
import com.bookrental.config.exceptions.ResourceNotFoundException;
import com.bookrental.persistence.PersistenceUtil;
import com.bookrental.persistence.constants.RentConstants;
import com.bookrental.persistence.entity.Book;
import com.bookrental.persistence.entity.Rent;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.RentRepository;
import com.bookrental.service.ServiceUtil;
import com.bookrental.service.book.BookService;
import com.bookrental.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
    private final ServiceUtil serviceUtil;
    private final PersistenceUtil persistenceUtil;
    private static final int DECREASE_BY_ONE = -1;

    @Autowired
    public RentService(UserService userService, BookService bookService,
                       RentRepository rentRepository, ServiceUtil serviceUtil, PersistenceUtil persistenceUtil) {
        this.userService = userService;
        this.bookService = bookService;
        this.rentRepository = rentRepository;
        this.serviceUtil = serviceUtil;
        this.persistenceUtil = persistenceUtil;
    }

    @Transactional
    public RentDto createRent(String bookPublicId, String userPublicId) {
        User user = userService.getUserRawEntityByPublicId(userPublicId);
        Book book = bookService.getBookRawEntityByPublicId(bookPublicId);
        bookService.changeBookAmount(bookPublicId, DECREASE_BY_ONE);

        Rent rent = Rent.builder()
                .publicId(serviceUtil.generateRandomUUID())
                .userPublicId(user.getPublicId())
                .bookPublicId(book.getPublicId())
                .user(user)
                .book(book)
                .endDate(null)
                .build();

        rentRepository.save(rent);

        return buildRent(rent);
    }

    public List<RentDto> getRents(String userPublicId, GetRentParams params) {
        String orderBy = params.getOrderBy() != null ? params.getOrderBy() : RentConstants.START_DATE.getValue();
        Pageable pageable = persistenceUtil.buildPageable(params.getSize(), params.getPage(), orderBy, params.getOrderDirection());
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

        return buildRent(rent);
    }

    private RentDto buildRent(Rent rent) {
        return RentDto.builder()
                .publicId(rent.getPublicId())
                .userPublicId(rent.getUserPublicId())
                .bookPublicId(rent.getBookPublicId())
                .startDate(rent.getStartDate())
                .endDate(rent.getEndDate())
                .build();
    }

}