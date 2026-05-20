package com.inventory.management.stock.controller;

import com.inventory.management.product.service.ProductService;
import com.inventory.management.stock.model.StockTransaction;
import com.inventory.management.stock.service.StockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Autowired private StockService stockService;
    @Autowired private ProductService productService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("transactions", stockService.getAllTransactions());
        model.addAttribute("activePage", "stock");
        return "stock/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("transaction", new StockTransaction());
        model.addAttribute("products", productService.getAllActiveProducts());
        model.addAttribute("types", StockTransaction.TransactionType.values());
        model.addAttribute("activePage", "stock");
        return "stock/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute StockTransaction transaction,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllActiveProducts());
            model.addAttribute("types", StockTransaction.TransactionType.values());
            model.addAttribute("activePage", "stock");
            return "stock/form";
        }
        try {
            stockService.recordTransaction(transaction);
            ra.addFlashAttribute("success", "Stock transaction recorded!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/stock";
    }
}