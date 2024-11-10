package com.bookrental.persistence.repositories;

import com.bookrental.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT role FROM user WHERE email = :email", nativeQuery = true)
    String getRoleByEmail(@Param("email") String email);

    boolean existsByEmail(@Param("email") String email);

    @Query(value = "DELETE FROM user WHERE public_id = :publicId", nativeQuery = true)
    void deleteByPublicId(@Param("publicId") String publicId);

    @Query(value = "SELECT * FROM user WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM user WHERE public_id = :publicId", nativeQuery = true)
    Optional<User> getByPublicId(@Param("publicId") String publicId);

}
