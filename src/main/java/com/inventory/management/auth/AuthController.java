// src/main/java/com/invStock/invStock/auth/AuthController.java
package com.inventory.management.auth;

import com.inventory.management.user.model.User;
import com.inventory.management.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // If already logged in, go to dashboard
        if (session.getAttribute("loggedInUser") != null)
            return "redirect:/dashboard";
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        var userOpt = userService.login(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.getActive()) {
                model.addAttribute("error", "Your account has been deactivated.");
                return "auth/login";
            }
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userRole", user.getRole().name());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid username or password.");
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "You have been logged out.");
        return "redirect:/login";
    }
}