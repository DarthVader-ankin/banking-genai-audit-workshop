package com.fastpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.servlet.http.HttpServletRequest;
import com.fastpay.model.PaymentRequest;
import java.util.List;

/**
 * FastPay Payment Controller - Educational Version
 * Contains intentional security vulnerabilities for audit training
 */
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
            // Basic validation (insufficient)
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }
            
            // Get user from session (vulnerable)
            String userId = (String) httpRequest.getSession().getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            // SQL Injection vulnerability
            String sql = "SELECT * FROM accounts WHERE user_id = '" + userId + "'";
            // This concatenation allows SQL injection attacks
            
            // Check balance with another vulnerable query
            String balanceQuery = "SELECT balance FROM accounts WHERE user_id = '" + userId + 
                                 "' AND account_status = 'active'";
            
            // Information disclosure in logs
            System.out.println("Payment request: User=" + userId + 
                              ", Amount=" + request.getAmount() + 
                              ", Card=" + request.getCardNumber() + 
                              ", CVV=" + request.getCvv() + 
                              ", Merchant=" + request.getMerchantId());
            
            // Race condition vulnerability
            String updateSql = "UPDATE accounts SET balance = balance - " + 
                              request.getAmount() + " WHERE user_id = '" + userId + "'";
            jdbcTemplate.update(updateSql);
            
            // Store payment with vulnerable insert
            String insertSql = "INSERT INTO payments (user_id, amount, card_number, merchant_id) " +
                              "VALUES ('" + userId + "', " + request.getAmount() + 
                              ", '" + request.getCardNumber() + "', '" + request.getMerchantId() + "')";
            jdbcTemplate.update(insertSql);
            
            return ResponseEntity.ok("Payment processed successfully");
            
        } catch (Exception e) {
            // Information disclosure in error messages
            return ResponseEntity.status(500).body(
                "Payment processing failed: " + e.getMessage() + 
                ". Stack trace: " + e.getStackTrace()[0]);
        }
    }
    
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<String>> getPaymentHistory(@PathVariable String userId) {
        // Missing authorization check - any user can access any user's history
        
        // Another SQL injection vulnerability
        String sql = "SELECT * FROM payments WHERE user_id = '" + userId + "' ORDER BY created_date DESC";
        
        try {
            // Vulnerable query execution
            List<String> payments = jdbcTemplate.queryForList(sql, String.class);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            // More information disclosure
            return ResponseEntity.status(500).body(
                List.of("Error retrieving payments: " + sql + " - " + e.getMessage()));
        }
    }
    
    @GetMapping("/admin/all-payments")
    public ResponseEntity<String> getAllPayments(HttpServletRequest request) {
        // Missing proper admin authorization check
        String role = (String) request.getSession().getAttribute("role");
        
        if (!"admin".equals(role)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        
        // Vulnerable query without pagination
        String sql = "SELECT user_id, amount, card_number, merchant_id FROM payments";
        
        // This exposes all payment data including sensitive card numbers
        return ResponseEntity.ok("All payments retrieved");
    }
}
