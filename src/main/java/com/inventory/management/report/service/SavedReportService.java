package com.inventory.management.report.service;

import com.inventory.management.report.model.SavedReport;

import java.util.List;
import java.util.Optional;

public interface SavedReportService {

    // Create
    SavedReport saveReport(SavedReport report);

    // Read
    List<SavedReport> getAllSavedReports();
    Optional<SavedReport> getSavedReportById(Long id);

    // Update
    SavedReport updateReport(Long id, SavedReport updated);

    // Delete
    void deleteReport(Long id);

    // Filter
    List<SavedReport> getReportsByType(SavedReport.ReportType type);
}