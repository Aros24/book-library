package com.bookrental.persistence.repositories;

import com.bookrental.persistence.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Modifying
    @Query("UPDATE Book b SET b.amount = b.amount + :amountToIncrement WHERE b.publicId = :publicId")
    int changeBookAmount(@Param("publicId") String bookPublicId, @Param("amountToIncrement") int amountToIncrement);

    List<Book> findAll(Specification<Book> specification, Pageable pageable);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.publicId = :publicId")
    Book findBookByPublicId(@Param("publicId") String publicId);

    @Query(value = "SELECT * FROM book WHERE isbn = :isbn", nativeQuery = true)
    Book findBookByIsbn(@Param("isbn") String isbn);

}
