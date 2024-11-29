package com.bookrental.persistence.repositories;

import com.bookrental.persistence.entity.Author;
import com.bookrental.persistence.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    List<Author> findAll(Specification<Author> specification, Pageable pageable);

    @Query(value = "SELECT * FROM author WHERE public_id IN (:publicIds)", nativeQuery = true)
    List<Author> findByPublicIdIn(@Param("publicIds") List<String> publicIds);

}
