package com.bookrental.persistence;

import com.bookrental.api.user.request.GetUserAccountParams;
import com.bookrental.config.exceptions.BadRequestException;
import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PersistenceUtil {

    private static final int MIN_SIZE = 1;
    private static final int MIN_PAGE = 0;
    private static final String ID_CONSTANT = "id";

    public Pageable buildPageable(Integer size, Integer page, String orderBy, String orderDirection) {
        if (size == null || size < MIN_SIZE) {
            throw new BadRequestException("Invalid size");
        }
        if (page == null || page < MIN_PAGE) {
            throw new BadRequestException("Invalid page number");
        }
        String order = orderBy != null ? orderBy : ID_CONSTANT;
        Sort.Direction direction = Sort.Direction.DESC.toString().equalsIgnoreCase(orderDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, order));
    }

    public Specification<User> buildUserListSpecification(GetUserAccountParams params) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<>();

            if (params.getFirstName() != null) {
                predicates.add(cb.like(root.get(UserConstants.FIRST_NAME.getValue()), wrapForLikeQuery(params.getFirstName())));
            }
            if (params.getLastName() != null) {
                predicates.add(cb.like(root.get(UserConstants.LAST_NAME.getValue()), wrapForLikeQuery(params.getLastName())));
            }
            if (params.getEmail() != null) {
                predicates.add(cb.like(root.get(UserConstants.EMAIL.getValue()), wrapForLikeQuery(params.getEmail())));
            }
            if (params.getRole() != null) {
                predicates.add(cb.equal(root.get(UserConstants.ROLE.getValue()), params.getRole()));
            }
            if (params.getDeleted() != null) {
                predicates.add(cb.equal(root.get(UserConstants.DELETED.getValue()), params.getDeleted()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Author> buildAuthorListSpecification(String name) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(root.get("name"), wrapForLikeQuery(name)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String wrapForLikeQuery(String value) {
        return String.format("%%%s%%", value);
    }

}
