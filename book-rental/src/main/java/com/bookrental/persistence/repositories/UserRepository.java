package com.bookrental.persistence.repositories;

import com.bookrental.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT role FROM user WHERE public_id = :publicId", nativeQuery = true)
    String getRoleByPublicId(@Param("publicId") String publicId);

}
