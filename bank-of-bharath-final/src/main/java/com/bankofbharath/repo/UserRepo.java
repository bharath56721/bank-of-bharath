package com.bankofbharath.repo;

import com.bankofbharath.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByAccountNumber(String accountNumber);
    Optional<User> findByAccountNumberOrEmail(String accountNumber, String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.accountNumber = :a")
    Optional<User> lockByAccountNumber(@Param("a") String a);
}
