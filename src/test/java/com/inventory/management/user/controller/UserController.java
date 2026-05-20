package com.inventory.management.user.controller;

import com.inventory.management.user.model.User;
import com.inventory.management.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("activePage", "users");
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activePage", "users");
        return "users/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute User user, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activePage", "users");
            return "users/form";
        }
        try {
            userService.saveUser(user);
            ra.addFlashAttribute("success", "User created successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return userService.getUserById(id).map(user -> {
            model.addAttribute("user", user);
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "users");
            return "users/form";
        }).orElseGet(() -> { ra.addFlashAttribute("error", "User not found."); return "redirect:/users"; });
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @Valid @ModelAttribute User user,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activePage", "users");
            return "users/form";
        }
        try {
            userService.updateUser(id, user);
            ra.addFlashAttribute("success", "User updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.deleteUser(id);
            ra.addFlashAttribute("success", "User deactivated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }
}