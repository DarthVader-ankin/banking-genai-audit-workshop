package com.fastpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/admin")
public class AdminController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "100") int limit,
                                        HttpServletRequest request) {
        
        // Vulnerability: No proper admin authorization check
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return ResponseEntity.status(401).body("Authorization required");
        }
        
        // Vulnerability: Weak admin validation
        if (!authHeader.contains("admin")) {
            return ResponseEntity.status(403).body("Admin access required");
        }
        
        // Vulnerability: SQL injection through limit parameter
        String sql = "SELECT user_id, email, role, created_date FROM users LIMIT " + limit;
        
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            // Vulnerability: Information disclosure
            return ResponseEntity.status(500).body("Query failed: " + sql + " - " + e.getMessage());
        }
    }
    
    @GetMapping("/payments/all")
    public ResponseEntity<?> getAllPayments() {
        
        // Vulnerability: No authorization check at all
        String sql = "SELECT user_id, amount, card_number, merchant_id, created_date FROM payments";
        
        try {
            List<Map<String, Object>> payments = jdbcTemplate.queryForList(sql);
            // Vulnerability: Exposing sensitive payment data including full card numbers
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database error: " + e.getMessage());
        }
    }
    
    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable String userId,
                                              @RequestBody Map<String, String> request) {
        
        // Vulnerability: No validation if admin can reset this user's password
        String newPassword = request.get("newPassword");
        
        // Vulnerability: No password complexity requirements
        if (newPassword == null || newPassword.length() < 3) {
            return ResponseEntity.badRequest().body("Password too short");
        }
        
        // Vulnerability: SQL injection and plaintext password storage
        String sql = "UPDATE users SET password = '" + newPassword + 
                    "' WHERE user_id = '" + userId + "'";
        
        try {
            jdbcTemplate.update(sql);
            
            // Vulnerability: Returning new password in response
            return ResponseEntity.ok(Map.of(
                "message", "Password reset successfully", 
                "newPassword", newPassword,
                "userId", userId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Password reset failed: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        
        // Vulnerability: No authorization check for user deletion
        // Vulnerability: SQL injection
        String sql = "DELETE FROM users WHERE user_id = '" + userId + "'";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql);
            
            if (rowsAffected > 0) {
                return ResponseEntity.ok("User deleted: " + userId);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Deletion failed: " + sql);
        }
    }
}
