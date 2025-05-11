# Siemens Java Internship - Code Refactoring Project 2025
This is a Spring Boot REST API developed as part of the Siemens Internship application project.
The application manages a list of `Item` objects, allowing full CRUD operations and asynchronous item processing using `CompletableFuture`.

---
# How to Run:

1. Clone this repository:
   ```bash
   git clone https://github.com/ruxandra1005/internship-application.git

2. Open in IntelliJ / VS Code

Run the main() method in InternshipApplication.java

3. Connect to H2 Console

Access the database UI at: http://localhost:8080/h2-console
In the application.properties file you get this URL: jdbc:h2:mem:testdb
No username, no password, you test the connection and then run.

4. Access the API at:
http://localhost:8080/api/items
using POSTMAN

 # Endpoints for POSTMAN
-- GET /api/items
Returns a list of all items.

-- GET /api/items/{id}
Returns a single item by ID.

- 200 OK if found
- 404 Not Found otherwise

-- POST /api/items
Creates a new item.

- 201 Created if valid
- 400 Bad Request + list of errors if invalid

-- PUT /api/items/{id}
Updates an existing item by ID.

- 200 OK if updated
- 400 Bad Request if validation fails
- 404 Not Found if ID doesn’t exist

-- DELETE /api/items/{id}
Deletes an item by ID.

- 204 No Content if deleted
- 404 Not Found if ID doesn’t exist

-- GET /api/items/process
Processes all items asynchronously.
Returns a list of updated items (status = "PROCESSED").

# Validation Rules
-- status: must not be blank
-- email: must be a well-formed email address
-- name, description: optional (customizable)
