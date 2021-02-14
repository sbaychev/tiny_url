# Tiny.Url

**An application making shortened urls**

---

***Technologies Used and Needed for Successful Running***

- Java 11
- Spring Boot Based
- Rest
- Repository Pattern (JPA | Hibernate + Code First Entity Definitions)
- Jackson | JSON pretty print output
- MySql8 DB (considerations for NoSql, pending usage) 
- H2 for Tests 
- Mockito + Spring JUnit5 Tests  
- Lombok as utility
- Swagger (consideration for addition)
- Maven
- Dockerized

---

**How to Run the Application**

1. The application is Dockerized, but requires the jar file be build, so from the root do:
   a) execute mvn clean install
   b) docker-compose up | docker-compose up -d (in case of failure re-run it)
   => services should be running | to verify execute curl http://localhost:8081/tiny/all -> should return List of 1 
   Entity
   c) docker-compose stop (as usual stops the execution of the Docker instances)
  

2. Application can be executed via the command `mvn spring-boot:run` or by simply running as executable the 
TinyUrlApplication.java file

---

- There is sample Single Record Created on Application Startup


- Sample tiny.url key that would be used as placeholder for functionality description below - h9uTci2j28

## Main Function Requirements Description

* The following Functionalities are supported via curl CLI or PostMan:

---
**Creation of shorten url:**

curl -X "POST" "http://localhost:8081/tiny" -i -H 'Content-Type: application/json' -d $'{"original_url": "https://stackoverflow.com/"}'

--- 
**List all shortened Urls**

curl http://localhost:8081/tiny/all --> Returns All Tiny Url

---

**See how many times a shortened url has been called**

curl http://localhost:8081/tiny/h9uTci2j28

---

**Shorten Urls Redirects to Original**

http://localhost:8081/h9uTci2j28 --> Use in any Browser and expected Behaviour is that whatever the tiny url would
automatically redirect

---

**All Important Messages are being Logged out to the CLI - Showing the Original vs Shortened URL**

---

## Architectural Design and Implementations

- Reasonable assumptions are that Reading vs Writing operations would be a magnitude more


- Have purposefully separated the application in **Read** and **Write** functional components
with the end goal of microservices separation of concerns as future workings
  

- All heavy reading and writing operations have been implemented asynchronously as we would like to handle
the to be more reading operations proper.
  

- Uniqueness of the url shortening key is guaranteed for a large set of possible values be it same original urls or 
  different. Collisions of the generated shortened url are hardly impossible for the most use-cases of people usage and
  even to a degree machine to machine (malicious or not intentionally such)
  

- The Function Generating the key could be extracted as seperate service per say. But in any case as writes are 
  expected to be lower, its complexity and execution are relatively cheap O(n) for the SHA-256 in similar fashion is 
  the Base64 encoding.
  

- The current implementation is as is a monolith. Should be able to handle > 500 r/s . Once sufficient time has passed
  being online. The microservices considerations can be properly addressed by splitting the 2 (3) important 
  operations into separate physical service for READING | WRITING Data (Key/ URL Generation also)
  
- General Assumption is that the creation of shortened | tiny urls is set to live for 30 days since the creation date.
  Thereupon, they are deleted via sql procedure or via the services themselves