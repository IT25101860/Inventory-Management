package com.inventory.management.order.controller;

import com.inventory.management.order.model.Order;
import com.inventory.management.order.service.OrderService;
import com.inventory.management.product.service.ProductService;
import com.inventory.management.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private ProductService productService;
    @Autowired private SupplierService supplierService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("activePage", "orders");
        return "orders/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("products", productService.getAllActiveProducts());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("statuses", Order.OrderStatus.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "orders");
        return "orders/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Order order, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllActiveProducts());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("statuses", Order.OrderStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activePage", "orders");
            return "orders/form";
        }
        try { orderService.saveOrder(order); ra.addFlashAttribute("success", "Order placed!"); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/orders";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return orderService.getOrderById(id).map(o -> {
            model.addAttribute("order", o);
            model.addAttribute("products", productService.getAllActiveProducts());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("statuses", Order.OrderStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "orders");
            return "orders/form";
        }).orElseGet(() -> { ra.addFlashAttribute("error", "Order not found."); return "redirect:/orders"; });
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status, RedirectAttributes ra) {
        try { orderService.updateOrderStatus(id, status); ra.addFlashAttribute("success", "Status updated!"); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/orders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { orderService.deleteOrder(id); ra.addFlashAttribute("success", "Order deleted."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/orders";
    }
}