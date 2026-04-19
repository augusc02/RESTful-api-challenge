package com.challenge.api.domain.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Payload to create or update a transaction")
public class TransactionRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Schema(description = "Monetary amount of the transaction", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double amount;

    @NotBlank(message = "Type is required")
    @Schema(description = "Category or type label for the transaction", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "Id of the parent transaction. Use 0 if there is no parent.")
    @JsonProperty("parent_id")
    private Long parentId;
}
