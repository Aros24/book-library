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

    @Query(value = "DELETE FROM author WHERE public_id = :publicId", nativeQuery = true)
    void deleteByPublicId(@Param("publicId") String publicId);

    List<Author> findAll(Specification<Author> specification, Pageable pageable);

}
