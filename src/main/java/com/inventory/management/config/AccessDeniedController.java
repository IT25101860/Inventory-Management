// src/main/java/com/invStock/invStock/config/AccessDeniedController.java
package com.inventory.management.config;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("activePage", "");
        return "auth/access-denied";
    }
}