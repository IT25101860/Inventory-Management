package com.inventory.management.supplier.controller;

import com.inventory.management.supplier.model.Supplier;
import com.inventory.management.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired private SupplierService supplierService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("suppliers", search != null && !search.isBlank()
                ? supplierService.searchSuppliers(search) : supplierService.getAllSuppliers());
        model.addAttribute("search", search);
        model.addAttribute("activePage", "suppliers");
        return "suppliers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "suppliers");
        return "suppliers/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Supplier supplier, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("activePage", "suppliers");
            return "suppliers/form";
        }
        try { supplierService.saveSupplier(supplier); ra.addFlashAttribute("success", "Supplier added!"); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/suppliers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return supplierService.getSupplierById(id).map(s -> {
            model.addAttribute("supplier", s);
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "suppliers");
            return "suppliers/form";
        }).orElseGet(() -> { ra.addFlashAttribute("error", "Supplier not found."); return "redirect:/suppliers"; });
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Supplier supplier,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "suppliers");
            return "suppliers/form";
        }
        try { supplierService.updateSupplier(id, supplier); ra.addFlashAttribute("success", "Supplier updated!"); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/suppliers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try { supplierService.deleteSupplier(id); ra.addFlashAttribute("success", "Supplier removed."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/suppliers";
    }
}