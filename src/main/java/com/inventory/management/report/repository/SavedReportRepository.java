package com.inventory.management.report.repository;

import com.inventory.management.report.model.SavedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedReportRepository extends JpaRepository<SavedReport, Long> {

    List<SavedReport> findAllByOrderByCreatedAtDesc();

    List<SavedReport> findByReportType(SavedReport.ReportType reportType);

    List<SavedReport> findByGeneratedBy(String generatedBy);
}