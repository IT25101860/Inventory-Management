package com.inventory.management.dashboard.controller;

import com.inventory.management.order.service.OrderService;
import com.inventory.management.product.service.ProductService;
import com.inventory.management.stock.service.StockService;
import com.inventory.management.supplier.service.SupplierService;
import com.inventory.management.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired private ProductService productService;
    @Autowired private SupplierService supplierService;
    @Autowired private OrderService orderService;
    @Autowired private StockService stockService;
    @Autowired private UserService userService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("totalSuppliers", supplierService.countSuppliers());
        model.addAttribute("totalOrders", orderService.countOrders());
        model.addAttribute("pendingOrders", orderService.countPendingOrders());
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts());
        model.addAttribute("recentTransactions", stockService.getAllTransactions().stream().limit(5).toList());
        model.addAttribute("recentOrders", orderService.getAllOrders().stream().limit(5).toList());
        model.addAttribute("activePage", "dashboard");
        return "dashboard/index";
    }
}