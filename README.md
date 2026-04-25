## Json Web Token (JWT) Authentication 

Implementation of JWT using spring boot security v6 and spring framework boot v4 as a shared library

**Components:**
- jwt-auth-starter: no impl but a default set of dependencies
- jwt-auth-autoconfigure: Implementation with the help of spring boot 
- jwt-auth-core: Implementations that are not dependent on any specific framework  
- jwt-auth-test: Test framework and also Integration + System tests

### Token generation 
Tokens can be generated for existing users, either during registration of a new user or when user request to get new token (e.g. if the token is expired). 

This implementation ca use HS algorithm or RSA. If HS algorithm is used, token creation and verification are done using same key i.e. the secret key.
For RSA algorithm, token signed with private key during token creation, and the verification is done using public key. 

### Authentication flow:

1. Accessing protected path:
We first verify the token using the key. If it is valid, we decode it and extract the subject. Then we optionally check if that subject corresponds to a valid user in our system.
The token is validated using the key which will either return a subject or null. then we check if the subject exists then we check whether the token is valid. 

![RequestToProtectedPath](./src/main/resources/img/requestToProtectedPath.png)

### Class diagram

![JwtUML](./src/main/resources/img/JwtUML.png)



