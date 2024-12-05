package com.bookrental.persistence.repositories;

import com.bookrental.persistence.entity.Rent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentRepository extends JpaRepository<Rent, Integer> {

    List<Rent> findAll(Specification<Rent> specification, Pageable pageable);

    @Query(value = "SELECT r.* FROM rent r " +
            "JOIN book b ON r.book_id = b.id " +
            "JOIN book_author ba ON b.id = ba.book_id " +
            "JOIN author a ON ba.author_id = a.id " +
            "WHERE r.public_id = :publicId", nativeQuery = true)
    Optional<Rent> findByPublicId(@Param("publicId") String publicId);

}
