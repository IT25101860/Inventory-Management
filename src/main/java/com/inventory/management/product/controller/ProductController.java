package com.inventory.management.product.controller;

import com.inventory.management.product.model.Product;
import com.inventory.management.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isBlank())
            model.addAttribute("products", productService.searchProducts(search));
        else
            model.addAttribute("products", productService.getAllActiveProducts());
        model.addAttribute("search", search);
        model.addAttribute("categories", Product.Category.values());
        model.addAttribute("lowStockCount", productService.getLowStockProducts().size());
        model.addAttribute("activePage", "products");
        return "products/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", Product.Category.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "products");
        return "products/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute Product product,
                         BindingResult result,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         Model model,
                         RedirectAttributes ra) {
        if (productService.isSkuTaken(product.getSku(), null))
            result.rejectValue("sku", "duplicate", "SKU already in use.");
        if (result.hasErrors()) {
            model.addAttribute("categories", Product.Category.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activePage", "products");
            return "products/form";
        }
        try {
            productService.saveProductWithImage(product, imageFile);
            ra.addFlashAttribute("success", "Product created!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return productService.getProductById(id).map(p -> {
            model.addAttribute("product", p);
            model.addAttribute("categories", Product.Category.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "products");
            return "products/form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Product not found.");
            return "redirect:/products";
        });
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Product product,
                         BindingResult result,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         Model model,
                         RedirectAttributes ra) {
        if (productService.isSkuTaken(product.getSku(), id))
            result.rejectValue("sku", "duplicate", "SKU already taken.");
        if (result.hasErrors()) {
            model.addAttribute("categories", Product.Category.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "products");
            return "products/form";
        }
        try {
            productService.updateProductWithImage(id, product, imageFile);
            ra.addFlashAttribute("success", "Product updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("success", "Product deactivated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }
}