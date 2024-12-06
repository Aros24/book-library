package com.bookrental.persistence;

import com.bookrental.persistence.entity.Book;
import com.bookrental.persistence.entity.Rent;
import com.bookrental.persistence.entity.User;
import com.bookrental.persistence.repositories.RentRepository;
import com.bookrental.service.ServiceUtil;
import com.bookrental.service.book.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RentDbAccessor {

    private final RentRepository rentRepository;
    private final BookService bookService;
    private final ServiceUtil serviceUtil;
    private static final int DECREASE_BY_ONE = -1;

    @Autowired
    public RentDbAccessor(RentRepository rentRepository, BookService bookService, ServiceUtil serviceUtil) {
        this.rentRepository = rentRepository;
        this.bookService = bookService;
        this.serviceUtil = serviceUtil;
    }

    @Transactional
    public Rent finishRentCreation(User user, Book book) {
        bookService.changeBookAmount(book.getPublicId(), DECREASE_BY_ONE);

        Rent rent = Rent.builder()
                .publicId(serviceUtil.generateRandomUUID())
                .userPublicId(user.getPublicId())
                .bookPublicId(book.getPublicId())
                .user(user)
                .book(book)
                .endDate(null)
                .build();

        rentRepository.save(rent);
        return rent;
    }

}
