# Banking Technology Audit - GenAI Use Cases

## Objective
This repository provides **practical use cases** for leveraging **Generative AI tools** in **technology audits** within banking environments.

## Control Testing Coverage

This workshop covers three critical areas of technology audit control testing:

### 1. **Application Security Controls**
- Code review for security vulnerabilities
- Input validation and sanitization testing
- Authentication and authorization controls
- Data protection and encryption validation

### 2. **API Security Controls** 
- Authentication mechanism testing
- Authorization and access control validation
- Rate limiting and abuse prevention
- Input/output security assessment

### 3. **Security Monitoring Controls**
- Log analysis and event correlation
- Fraud detection and anomaly identification
- Compliance monitoring and reporting
- Incident detection and response

## Case Study: FastPay Application

**Fictional Banking Application:** FastPay - A distributed payment processing system

**Architecture:**
- Java Spring Boot microservices
- AWS cloud infrastructure
- PostgreSQL database
- Redis caching layer
- RESTful APIs for mobile/web clients

**Components Included:**
- Payment processing service
- Authentication and authorization modules
- API gateway and security filters
- Database access layers
- Logging and monitoring systems

## Repository Artifacts

### **Use Case 1: Payment Code Review**
- **Location:** `use-case-1-payment-code-review/`
- **Contains:** Java source code with intentional vulnerabilities
- **GenAI Prompts:** Code security assessment prompts
- **Expected Findings:** Vulnerability reports and remediation guidance

### **Use Case 2: API Security Assessment**
- **Location:** `use-case-2-api-security/`
- **Contains:** OpenAPI specifications, authentication code, security filters
- **GenAI Prompts:** API security evaluation prompts
- **Expected Findings:** OWASP API Top 10 assessment results

### **Use Case 3: Splunk Log Analysis**
- **Location:** `use-case-3-splunk-log-analysis/`
- **Contains:** Sample JSON logs, Splunk queries, security event data
- **GenAI Prompts:** Log analysis and fraud detection prompts
- **Expected Findings:** Security incident reports and compliance summaries

## Getting Started

1. **Choose a use case** from the three options above
2. **Review the artifacts** in the corresponding directory
3. **Use the provided GenAI prompts** with tools like ChatGPT or Claude
4. **Compare your results** with the expected findings

## Compliance Context

All use cases are designed to support:
- **APRA CPS 234** Information Security requirements
- **PCI-DSS** Payment Card Industry standards
- **ISO 27001** Security management principles
- **OWASP** Security testing methodologies

---

**Note:** All code contains intentional vulnerabilities for educational purposes. Do not use in production environments.
