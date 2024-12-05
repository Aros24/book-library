package com.bookrental.persistence.repositories;

import com.bookrental.persistence.entity.Rent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentRepository extends JpaRepository<Rent, Integer> {

    List<Rent> findAll(Specification<Rent> specification, Pageable pageable);

    Optional<Rent> findByPublicId(String publicId);

}
