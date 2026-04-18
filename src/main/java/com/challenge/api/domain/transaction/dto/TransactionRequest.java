package com.challenge.api.domain.transaction.dto;

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
    @Schema(description = "Monetary amount of the transaction", example = "150.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double amount;

    @NotBlank(message = "Type is required")
    @Schema(description = "Category or type label for the transaction", example = "car", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotNull(message = "Parent id is required")
    @Schema(description = "Id of the parent transaction. Use 0 if there is no parent.", example = "0")
    private Long parentId;
}
