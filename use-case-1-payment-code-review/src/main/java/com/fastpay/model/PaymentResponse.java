package com.fastpay.model;

public class PaymentResponse {
    private String status;
    private String message;
    private String transactionId;
    
    public PaymentResponse(String message, String transactionId) {
        this.message = message;
        this.transactionId = transactionId;
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
