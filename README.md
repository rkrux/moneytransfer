## Money Transfer Service [![Build Status](https://travis-ci.com/rkrux/moneytransfer.svg?branch=master)](https://travis-ci.com/rkrux/moneytransfer)

#### Business Features
- Create few sample bank accounts on startup
- Allow the user to add more bank accounts through a POST API
- Retrieve all accounts through a GET API
- Transfer funds between 2 accounts
- Handles synchronization in a multi-threaded scenario, ensuring data integrity

#### Build And Run
- Environment: 
    ~~~~
    OpenJDK 8
    ~~~~
- Build: 
    ~~~~
    mvn clean install
    ~~~~
- Run:
    ~~~~
    java -jar target/moneytransfer-1.0-SNAPSHOT.jar
    ~~~~

#### Testing
- Numerous extensive test cases cover this service
    - Unit Tests
    - Functional Tests
    - Integration Tests
    - Multi-Threaded Integration Tests, several parallel threads started in **TransferAndGetIntegrationParallelTest.java**

#### AddBankAccount POST API
- Endpoint: /bankAccounts/add
- Returns HTTP 200 OK on successful request without body
- Sample Request:
    ~~~~
    {
        "id": 10,
        "balance": 1
    }
    ~~~~
- Errors:
    - HTTP 404 with "Incomplete transfer parameters" in Response body
    - HTTP 412 with "Illegal add account parameters" in Response body
    - HTTP 412 with "Account already present" in Response body
    
#### GetAllAccounts GET API
- Endpoint: /bankAccounts/all
- Returns HTTP 200 OK on successful request with body
- Sample Body:
    ~~~~
    {
        "allBankAccounts": [
            {
                "id": 1,
                "balance": 100.00
            },
            {
                "id": 2,
                "balance": 50.00
            },
            {
                "id": 3,
                "balance": 20.52
            },
            {
                "id": 4,
                "balance": 30.61
            }
        ]
    }
    ~~~~

#### TransferMoney POST API
- Endpoint: /transferMoney
- Returns HTTP 200 OK on successful request with body
- Sample Request:
    ~~~~
    {
        "from": 1,
        "to": 2,
        "amount": 10
    }
    ~~~~
- Sample Body: 
    ~~~~
    {
        "updatedAccounts": {
            "from": {
                "id": 1,
                "balance": 90.00
            },
            "to": {
                "id": 2,
                "balance": 60.00
            }
        }
    }
     ~~~~
- Errors:
    - HTTP 404 with "Incomplete transfer parameters" in Response body
    - HTTP 412 with "Can't transfer funds to self" in Response body
    - HTTP 412 with "Only positive amount transfer allowed" in Response body
    - HTTP 412 with "Account not found" in Response body
    - HTTP 412 with "Account balance insufficient for this transfer" in Response body
    
#### Dependencies
   - Jetty server 9
   - Jersey 2
   - Jackson 2
   - Junit 4
   - Vmlens Concurrent JUnit
   - Jersey Test Framework Provider Jetty
   - lombok
  
