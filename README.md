# Payment Gateway Challenge
A simple API to allow an ecommerce merchant to accept a payment from a customer via an acquiring bank.

# Running the application
Choose one of the following:
- In IntelliJ directly
- `JAVA_HOME=/path/to/java17 mvn spring-boot:run`
- `docker-compose up --build`

API Documentation:
- Try out the API through SwaggerUI at http://localhost:8080/swagger-ui/index.html
- Authorize with:
  - ```username: ec721f72-633e-4c73-ae14-3a757998291d``` (this is the merchant id)
  - ```password: password```
- Enter a CVC of `000` to trigger an error in the acquiring bank when processing a payment

# Running the tests
Choose one of the following:
- In IntelliJ directly
- `JAVA_HOME=/path/to/java17 mvn test`
- `docker run -it --rm -v $PWD:$PWD -w $PWD maven:3-openjdk-17-slim mvn test`

# Assumptions
- Acquiring banks can only support a limited range of currencies. The system can be extended by creating another implementation of the AcquiringBankClient with additional supported currencies and this will be injected automatically by the framework and used to process requests.
- The payment can be made synchronously in a single call. In the real world this would most likely be a multi-step process, eg. 
    - CreatePaymentIntent - sets up the payment details
    - AuthorisePaymentIntent - performs SCA
    - ExecutePaymentIntent - calls the acquiring bank

# Areas for improvement
- Use domain driven design principles and introduce java objects that correspond to the domain, eg. Merchant, Card etc.
- Make the SimulatedAcquiringBankClient more realistic with more validation and modes of failure
- Implement a retry method in case of a failed call to an acquiring bank. This could fall back to the next suitable bank in case there are multiple available. This could be achieved using the Feign library (https://github.com/OpenFeign/feign)
- Implement an idempotency key header to avoid processing duplicate payment requests
- Add logging to help with debugging and audit trail
- Store multiple user credentials in db or a secret store eg. Vault (https://www.vaultproject.io)
- Create a strategy to choose the best AcquiringBankClient in case of multiple clients that support the requested payment currency according to business rules
- Move validation out of the controller layer and into to service layer to allow extensive testing of validation rules
- Add public/private key request and response signing to enhance security. The signatures would be provided in request/response headers and verified by the application and merchant
- Add mutation testing with PITest library (http://pitest.org/) to ensure the quality of the tests beyond simple coverage
- Add JaCoCo (https://www.jacoco.org) to generate code coverage reports and enforce rules as part of the build pipeline  
- Add Checkstyle plugin (http://checkstyle.sourceforge.net/) to enforce coding standards as part of build

# Proposed cloud technologies
- AWS EC2 for multiple application instances according to performance requirements
- AWS ELB for load-balancing and SSL
- AWS RDS for replicated database with backups 
- Deployment with Terraform
