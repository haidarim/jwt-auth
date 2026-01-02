package io.github.haidarim.repository;

import io.github.haidarim.entity.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TestUserRepository extends JpaRepository<TestUser, Long> {

    @Query("SELECT u FROM TestUser u WHERE u.email = :email")
    TestUser findByEmail(@Param("email") String email);

    @Query("SELECT u.email FROM TestUser u WHERE u.username = :username")
    String findEmailByUsername(@Param("username") String username);

    @Query("SELECT u.email FROM TestUser u WHERE u.uniqueNumber = :uniqueNumber")
    String findEmailByUniqueNumber(@Param("uniqueNumber") String uniqueNumber);
}
