package com.bankofbharath.repo;

import com.bankofbharath.model.Txn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TxnRepo extends JpaRepository<Txn, Long> {
    List<Txn> findTop20ByAccountNumberOrderByCreatedAtDesc(String accountNumber);
    void deleteByAccountNumber(String accountNumber);
}
