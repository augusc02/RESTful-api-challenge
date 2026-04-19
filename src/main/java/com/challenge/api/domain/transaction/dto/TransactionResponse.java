package com.challenge.api.domain.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Transaction response payload")
public class TransactionResponse {

    @Schema(description = "Unique identifier of the transaction", example = "1")
    Long id;

    @Schema(description = "Monetary amount of the transaction", example = "150.0")
    Double amount;

    @Schema(description = "Category or type label for the transaction", example = "car")
    String type;

    @Schema(description = "Id of the parent transaction, or 0 if none", example = "0")
    @JsonProperty("parent_id")
    Long parentId;
}
