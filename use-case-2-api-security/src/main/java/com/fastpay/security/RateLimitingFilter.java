package com.fastpay.security;

import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class RateLimitingFilter implements Filter {
    
    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRequestTime = new ConcurrentHashMap<>();
    
    // Vulnerability: Hardcoded limits without configuration
    private final int MAX_REQUESTS = 1000;
    private final long TIME_WINDOW = 60000; // 1 minute
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Vulnerability: Using only IP address for rate limiting (easily bypassed)
        String clientIp = httpRequest.getRemoteAddr();
        
        // Vulnerability: No X-Forwarded-For header consideration
        // Vulnerability: Easy bypass with proxy rotation
        
        Integer count = requestCounts.get(clientIp);
        if (count == null) {
            count = 0;
        }
        
        // Vulnerability: No proper time window sliding
        Long lastTime = lastRequestTime.get(clientIp);
        if (lastTime != null && (System.currentTimeMillis() - lastTime) > TIME_WINDOW) {
            // Reset counter if time window passed
            count = 0;
        }
        
        count++;
        requestCounts.put(clientIp, count);
        lastRequestTime.put(clientIp, System.currentTimeMillis());
        
        if (count > MAX_REQUESTS) {
            // Vulnerability: Detailed rate limiting information disclosure
            httpResponse.setStatus(429);
            httpResponse.setHeader("X-Rate-Limit-Remaining", "0");
            httpResponse.setHeader("X-Rate-Limit-Reset", String.valueOf(TIME_WINDOW));
            httpResponse.getWriter().write(
                "Rate limit exceeded for IP: " + clientIp + 
                ". Requests: " + count + "/" + MAX_REQUESTS
            );
            return;
        }
        
        // Vulnerability: Rate limit information exposed in headers
        httpResponse.setHeader("X-Rate-Limit-Remaining", String.valueOf(MAX_REQUESTS - count));
        
        chain.doFilter(request, response);
    }
    
    // Vulnerability: No cleanup mechanism for old entries
    // Memory leak potential with many different IPs
}
