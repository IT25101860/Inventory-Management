// src/main/java/com/invStock/invStock/config/SessionAdvice.java
package com.inventory.management.config;

import com.inventory.management.user.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SessionAdvice {

    @ModelAttribute
    public void addSessionData(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            model.addAttribute("sessionUser", user);
            model.addAttribute("sessionRole", user.getRole().name());
        }
    }
}