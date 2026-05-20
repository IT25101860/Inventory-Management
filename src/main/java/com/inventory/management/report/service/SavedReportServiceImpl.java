package com.inventory.management.report.service;

import com.inventory.management.report.model.SavedReport;
import com.inventory.management.report.repository.SavedReportRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SavedReportServiceImpl implements SavedReportService {

    @Autowired
    private SavedReportRepository savedReportRepository;

    // ── CREATE ────────────────────────────────────────────
    @Override
    public SavedReport saveReport(SavedReport report) {
        return savedReportRepository.save(report);
    }

    // ── READ ──────────────────────────────────────────────
    @Override
    public List<SavedReport> getAllSavedReports() {
        return savedReportRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Optional<SavedReport> getSavedReportById(Long id) {
        return savedReportRepository.findById(id);
    }

    // ── UPDATE ────────────────────────────────────────────
    @Override
    public SavedReport updateReport(Long id, SavedReport updated) {
        SavedReport existing = savedReportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report not found: " + id));

        existing.setTitle(updated.getTitle());
        existing.setNotes(updated.getNotes());
        existing.setReportType(updated.getReportType());

        return savedReportRepository.save(existing);
    }

    // ── DELETE ────────────────────────────────────────────
    @Override
    public void deleteReport(Long id) {
        if (!savedReportRepository.existsById(id))
            throw new EntityNotFoundException("Report not found: " + id);
        savedReportRepository.deleteById(id);
    }

    // ── FILTER ────────────────────────────────────────────
    @Override
    public List<SavedReport> getReportsByType(SavedReport.ReportType type) {
        return savedReportRepository.findByReportType(type);
    }
}