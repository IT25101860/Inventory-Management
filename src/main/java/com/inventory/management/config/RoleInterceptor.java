// src/main/java/com/invStock/invStock/config/RoleInterceptor.java
package com.inventory.management.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null) return true; // AuthInterceptor handles redirect

        String role = (String) session.getAttribute("userRole");
        if (role == null) return true;

        String path = request.getRequestURI();
        String method = request.getMethod();

        // ── STAFF restrictions ───────────────────────────────────────────
        if ("STAFF".equals(role)) {
            // Staff cannot access user management at all
            if (path.startsWith("/users")) {
                response.sendRedirect("/access-denied");
                return false;
            }
            // Staff cannot add/edit/delete products
            if (path.startsWith("/products/new") || path.matches("/products/\\d+/edit")
                    || path.matches("/products/\\d+/delete")) {
                response.sendRedirect("/access-denied");
                return false;
            }
            // Staff cannot manage suppliers or orders
            if (path.startsWith("/suppliers/new") || path.matches("/suppliers/\\d+/edit")
                    || path.matches("/suppliers/\\d+/delete")) {
                response.sendRedirect("/access-denied");
                return false;
            }
        }

        // ── MANAGER restrictions ─────────────────────────────────────────
        if ("MANAGER".equals(role)) {
            // Manager cannot access user management
            if (path.startsWith("/users")) {
                response.sendRedirect("/access-denied");
                return false;
            }
            // Manager cannot delete products
            if (path.matches("/products/\\d+/delete")) {
                response.sendRedirect("/access-denied");
                return false;
            }
        }

        return true;
    }
}