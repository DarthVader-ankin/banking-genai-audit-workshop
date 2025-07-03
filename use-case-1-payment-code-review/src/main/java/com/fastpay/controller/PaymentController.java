package com.fastpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.servlet.http.HttpServletRequest;
import com.fastpay.model.PaymentRequest;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostMapping("/process")
    public ResponseEntity<String> processPayment(
            @RequestBody PaymentRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Vulnerability 1: Insufficient input validation
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            
            // Vulnerability 2: Insecure session management
            String userId = (String) httpRequest.getSession().getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            // Vulnerability 3: SQL Injection vulnerability
            String sql = "SELECT balance FROM accounts WHERE user_id = '" + userId + "'";
            
            // Vulnerability 4: Sensitive data exposure in logs
            System.out.println("Processing payment for user: " + userId + 
                       " Amount: " + request.getAmount() + 
                       " Card: " + request.getCardNumber());
            
            // Vulnerability 5: Race condition in balance updates
            String updateSql = "UPDATE accounts SET balance = balance - " + 
                              request.getAmount() + " WHERE user_id = '" + userId + "'";
            jdbcTemplate.update(updateSql);
            
            return ResponseEntity.ok("Payment processed successfully");
            
        } catch (Exception e) {
            // Vulnerability 6: Information disclosure in error messages
            return ResponseEntity.status(500).body("Payment failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/history/{userId}")
    public ResponseEntity<String> getPaymentHistory(@PathVariable String userId) {
        // Vulnerability 7: Missing authorization check
        String sql = "SELECT * FROM payments WHERE user_id = '" + userId + "'";
        return ResponseEntity.ok("Payment history retrieved");
    }
}
