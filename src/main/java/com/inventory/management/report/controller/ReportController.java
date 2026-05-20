package com.inventory.management.report.controller;

import com.inventory.management.order.model.Order;
import com.inventory.management.order.repository.OrderRepository;
import com.inventory.management.product.model.Product;
import com.inventory.management.product.repository.ProductRepository;
import com.inventory.management.report.model.SavedReport;
import com.inventory.management.report.service.SavedReportService;
import com.inventory.management.stock.model.StockTransaction;
import com.inventory.management.stock.repository.StockRepository;
import com.inventory.management.supplier.repository.SupplierRepository;
import com.inventory.management.user.model.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired private ProductRepository  productRepository;
    @Autowired private OrderRepository    orderRepository;
    @Autowired private StockRepository    stockRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private SavedReportService savedReportService;

    // ── MAIN REPORTS PAGE (Read) ──────────────────────────
    @GetMapping
    public String reports(Model model) {

        // ── PRODUCT STATS ─────────────────────────────────
        List<Product> allProducts      = productRepository.findAll();
        List<Product> activeProducts   = productRepository.findByActiveTrue();
        List<Product> lowStockProducts = productRepository.findLowStockProducts();

        model.addAttribute("totalProducts",    allProducts.size());
        model.addAttribute("activeProducts",   activeProducts.size());
        model.addAttribute("inactiveProducts", allProducts.size() - activeProducts.size());
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("lowStockCount",    lowStockProducts.size());

        // ── PRODUCTS BY CATEGORY ──────────────────────────
        Map<String, Long> productsByCategory = activeProducts.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().name().replace("_", " "),
                        Collectors.counting()));
        model.addAttribute("productsByCategory", productsByCategory);

        // ── TOTAL INVENTORY VALUE ─────────────────────────
        BigDecimal totalInventoryValue = activeProducts.stream()
                .map(p -> p.getPrice()
                        .multiply(BigDecimal.valueOf(p.getStockQuantity().longValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalInventoryValue", totalInventoryValue);

        // ── TOP 5 PRODUCTS BY STOCK VALUE ─────────────────
        // Calculated in Java — never use T(BigDecimal) in Thymeleaf
        List<Map<String, Object>> topProductsByValue = activeProducts.stream()
                .sorted((a, b) -> {
                    BigDecimal va = a.getPrice()
                            .multiply(BigDecimal.valueOf(a.getStockQuantity().longValue()));
                    BigDecimal vb = b.getPrice()
                            .multiply(BigDecimal.valueOf(b.getStockQuantity().longValue()));
                    return vb.compareTo(va);
                })
                .limit(5)
                .map(p -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("name",       p.getName());
                    map.put("stock",      p.getStockQuantity());
                    map.put("price",      p.getPrice());
                    map.put("totalValue", p.getPrice()
                            .multiply(BigDecimal.valueOf(p.getStockQuantity().longValue())));
                    return map;
                })
                .collect(Collectors.toList());
        model.addAttribute("topProductsByValue", topProductsByValue);

        // ── ORDER STATS ───────────────────────────────────
        List<Order> allOrders = orderRepository.findAll();

        model.addAttribute("totalOrders",     allOrders.size());
        model.addAttribute("pendingOrders",
                allOrders.stream()
                        .filter(o -> o.getStatus() == Order.OrderStatus.PENDING).count());
        model.addAttribute("confirmedOrders",
                allOrders.stream()
                        .filter(o -> o.getStatus() == Order.OrderStatus.CONFIRMED).count());
        model.addAttribute("shippedOrders",
                allOrders.stream()
                        .filter(o -> o.getStatus() == Order.OrderStatus.SHIPPED).count());
        model.addAttribute("deliveredOrders",
                allOrders.stream()
                        .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED).count());
        model.addAttribute("cancelledOrders",
                allOrders.stream()
                        .filter(o -> o.getStatus() == Order.OrderStatus.CANCELLED).count());

        // ── TOTAL ORDER VALUE (excluding cancelled) ───────
        BigDecimal totalOrderValue = allOrders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalOrderValue", totalOrderValue);

        // ── RECENT ORDERS (last 10) ───────────────────────
        List<Order> recentOrders = allOrders.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .collect(Collectors.toList());
        model.addAttribute("recentOrders", recentOrders);

        // ── STOCK TRANSACTION STATS ───────────────────────
        List<StockTransaction> allTransactions = stockRepository.findAll();

        model.addAttribute("totalTransactions", allTransactions.size());
        model.addAttribute("stockInCount",
                allTransactions.stream()
                        .filter(t -> t.getType() == StockTransaction.TransactionType.STOCK_IN)
                        .count());
        model.addAttribute("stockOutCount",
                allTransactions.stream()
                        .filter(t -> t.getType() == StockTransaction.TransactionType.STOCK_OUT)
                        .count());
        model.addAttribute("adjustCount",
                allTransactions.stream()
                        .filter(t -> t.getType() == StockTransaction.TransactionType.ADJUSTMENT)
                        .count());

        // ── RECENT TRANSACTIONS (last 10) ─────────────────
        List<StockTransaction> recentTransactions = allTransactions.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(10)
                .collect(Collectors.toList());
        model.addAttribute("recentTransactions", recentTransactions);

        // ── SUPPLIER STATS ────────────────────────────────
        model.addAttribute("totalSuppliers",
                supplierRepository.count());
        model.addAttribute("activeSuppliers",
                supplierRepository.findByActiveTrue().size());

        // ── SAVED REPORTS COUNT (for badge) ───────────────
        model.addAttribute("savedReportsCount",
                savedReportService.getAllSavedReports().size());

        // ── EMPTY FORM FOR SAVING A REPORT ────────────────
        model.addAttribute("newReport", new SavedReport());
        model.addAttribute("reportTypes", SavedReport.ReportType.values());

        model.addAttribute("activePage", "reports");
        return "reports/index";
    }

    // ── SAVE REPORT (Create) ──────────────────────────────
    @PostMapping("/save")
    public String saveReport(@Valid @ModelAttribute("newReport") SavedReport report,
                             BindingResult result,
                             HttpSession session,
                             RedirectAttributes ra,
                             Model model) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Please fill in all required fields.");
            return "redirect:/reports";
        }
        try {
            User user = (User) session.getAttribute("loggedInUser");
            report.setGeneratedBy(user != null ? user.getUsername() : "system");
            savedReportService.saveReport(report);
            ra.addFlashAttribute("success", "Report saved successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reports/saved";
    }

    // ── VIEW ALL SAVED REPORTS (Read) ─────────────────────
    @GetMapping("/saved")
    public String savedReports(Model model) {
        model.addAttribute("savedReports",
                savedReportService.getAllSavedReports());
        model.addAttribute("reportTypes",
                SavedReport.ReportType.values());
        model.addAttribute("activePage", "reports");
        return "reports/saved";
    }

    // ── SHOW EDIT FORM (Update) ───────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model,
                           RedirectAttributes ra) {
        return savedReportService.getSavedReportById(id).map(report -> {
            model.addAttribute("report", report);
            model.addAttribute("reportTypes", SavedReport.ReportType.values());
            model.addAttribute("activePage", "reports");
            return "reports/saved";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Report not found.");
            return "redirect:/reports/saved";
        });
    }

    // ── HANDLE EDIT SUBMIT (Update) ───────────────────────
    @PostMapping("/{id}/edit")
    public String updateReport(@PathVariable Long id,
                               @Valid @ModelAttribute("report") SavedReport report,
                               BindingResult result,
                               RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Please fix the errors.");
            return "redirect:/reports/saved";
        }
        try {
            savedReportService.updateReport(id, report);
            ra.addFlashAttribute("success", "Report updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reports/saved";
    }

    // ── DELETE (Delete) ───────────────────────────────────
    @PostMapping("/{id}/delete")
    public String deleteReport(@PathVariable Long id,
                               RedirectAttributes ra) {
        try {
            savedReportService.deleteReport(id);
            ra.addFlashAttribute("success", "Report deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reports/saved";
    }
}