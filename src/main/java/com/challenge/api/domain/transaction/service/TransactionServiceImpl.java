package com.challenge.api.domain.transaction.service;

import com.challenge.api.domain.transaction.dto.TransactionRequest;
import com.challenge.api.domain.transaction.dto.TransactionResponse;
import com.challenge.api.domain.transaction.mapper.TransactionMapper;
import com.challenge.api.domain.transaction.model.Transaction;
import com.challenge.api.domain.transaction.exception.SelfReferencingTransactionException;
import com.challenge.api.domain.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll() {
        log.debug("Fetching all transactions");
        return transactionMapper.toResponseList(transactionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> findByType(String type) {
        log.debug("Fetching transactions by type: {}", type);
        return transactionMapper.toResponseList(transactionRepository.findByType(type));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findIdsByType(String type) {
        log.debug("Fetching transaction ids by type: {}", type);
        return transactionRepository.findByType(type).stream()
                .map(Transaction::getId)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse findById(Long id) {
        log.debug("Fetching transaction with id: {}", id);
        return transactionMapper.toResponse(transactionRepository.findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Double sumByTransactionId(Long id) {
        log.debug("Computing sum for transaction id: {}", id);
        double total = 0;
        Queue<Transaction> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        
        Transaction initialTransaction = transactionRepository.findByIdOrThrow(id);
        queue.add(initialTransaction);
        visited.add(initialTransaction.getId());
        
        while (!queue.isEmpty()) {
            Transaction current = queue.poll();
            total += current.getAmount();
            
            for (Transaction child : transactionRepository.findByParentId(current.getId())) {
                if (visited.add(child.getId())) {
                    queue.add(child);
                }
            }
        }
        
        return total;
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> putTransaction(Long id, TransactionRequest request) {
        if (id.equals(request.getParentId())) {
            throw new SelfReferencingTransactionException(id);
        }

        boolean isNew = !transactionRepository.existsById(id);
        Transaction transaction;
        
        if (isNew) {
            transaction = transactionMapper.toEntity(request);
            transaction.setId(id);
        } else {
            transaction = transactionRepository.findByIdOrThrow(id);
            transactionMapper.updateEntityFromRequest(request, transaction);
        }
        
        transactionRepository.save(transaction);
        log.info("Transaction {} with id: {}", isNew ? "created" : "updated", id);
        
        HttpStatus status = isNew ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(Map.of("status", "ok"));
    }
}
