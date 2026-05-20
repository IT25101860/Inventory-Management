package com.inventory.management.report.model;

import com.inventory.management.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "saved_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SavedReport extends BaseEntity {

    @NotBlank(message = "Report title is required")
    @Size(min = 2, max = 100, message = "Title must be 2-100 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 500, message = "Notes too long")
    @Column(length = 500)
    private String notes;

    @NotNull(message = "Report type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Column(nullable = false)
    private String generatedBy;

    // ── Report Type Enum ──────────────────────────────────
    public enum ReportType {
        INVENTORY_SUMMARY,
        ORDER_SUMMARY,
        STOCK_ACTIVITY,
        SUPPLIER_SUMMARY,
        FULL_REPORT
    }

    // ── Business logic ────────────────────────────────────
    public String getReportTypeLabel() {
        return this.reportType.name().replace("_", " ");
    }
}