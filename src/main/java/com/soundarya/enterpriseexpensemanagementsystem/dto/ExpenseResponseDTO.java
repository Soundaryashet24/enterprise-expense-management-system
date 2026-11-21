
package com.soundarya.enterpriseexpensemanagementsystem.dto;

import com.soundarya.enterpriseexpensemanagementsystem.entity.ExpenseStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResponseDTO {

    private Long id;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDate expenseDate;
    private ExpenseStatus status;

    public ExpenseResponseDTO(
            Long id,
            BigDecimal amount,
            String category,
            String description,
            LocalDate expenseDate,
            ExpenseStatus status) {

        this.id = id;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.expenseDate = expenseDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public ExpenseStatus getStatus() {
        return status;
    }
}
