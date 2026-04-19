package com.challenge.api.domain.transaction.repository;

import com.challenge.api.domain.transaction.model.Transaction;
import com.challenge.api.domain.transaction.exception.TransactionNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByType(String type);

    List<Transaction> findByParentId(Long parentId);

    default Transaction findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }
}
