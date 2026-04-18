package com.challenge.api.domain.transaction.mapper;

import com.challenge.api.domain.transaction.dto.TransactionRequest;
import com.challenge.api.domain.transaction.dto.TransactionResponse;
import com.challenge.api.domain.transaction.model.Transaction;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;


@Mapper
public interface TransactionMapper {

    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);

    @Mapping(target = "id", ignore = true)
    Transaction toEntity(TransactionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(TransactionRequest request, @MappingTarget Transaction transaction);
}
