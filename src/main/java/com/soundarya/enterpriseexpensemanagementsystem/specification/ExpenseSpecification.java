
package com.soundarya.enterpriseexpensemanagementsystem.specification;

import com.soundarya.enterpriseexpensemanagementsystem.entity.Expense;
import com.soundarya.enterpriseexpensemanagementsystem.entity.ExpenseStatus;

import org.springframework.data.jpa.domain.Specification;

public class ExpenseSpecification {

    public static Specification<Expense> withFilters(
            ExpenseStatus status,
            String category,
            Long employeeId) {

        return (root, query, cb) -> {

            var predicates = new java.util.ArrayList<
                    jakarta.persistence.criteria.Predicate>();

            if (status != null) {
                predicates.add(
                        cb.equal(root.get("status"), status));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(
                        cb.equal(
                                cb.lower(root.get("category")),
                                category.trim().toLowerCase(
                                        java.util.Locale.ROOT)));
            }

            if (employeeId != null) {
                predicates.add(
                        cb.equal(
                                root.get("employee").get("id"),
                                employeeId));
            }

            return cb.and(predicates.toArray(
                    new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
