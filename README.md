# enterprise-expense-management-system

A RESTful backend application built with Java and Spring Boot to demonstrate structured backend development, employee data management, request validation, and centralized exception handling.

## Project Overview

The Enterprise Expense Management System is being developed as a modular backend application. The current implementation focuses on the Employee module, establishing the API and service-layer foundation for future expense-management functionality.

The Employee API supports creating, retrieving, updating, and deleting employee records stored in MySQL.

## Key Features

- RESTful CRUD APIs for employee management
- Layered architecture separating Controller, Service, and Repository responsibilities
- Spring Data JPA for database operations
- MySQL persistence
- Request and response DTOs to separate API models from database entities
- Input validation for employee names and email addresses
- Centralized exception handling
- Structured JSON error responses for validation failures and missing employees

## Technology Stack

- **Language:** Java
- **Backend framework:** Spring Boot
- **Persistence:** Spring Data JPA / Hibernate
- **Database:** MySQL
- **Build tool:** Maven
- **API testing:** Postman
- **Version control:** Git and GitHub

## Application Architecture

The application follows a layered architecture:

Client → REST Controller → Service → Repository → MySQL

- **Controller:** Handles HTTP requests and responses.
- **DTO:** Defines the data accepted from and returned to API clients.
- **Service:** Contains employee-related application logic.
- **Repository:** Uses Spring Data JPA to access employee records.
- **Entity:** Represents the employee database model.
- **Exception handler:** Converts supported application exceptions into structured HTTP error responses.

## Employee API

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/employees` | Retrieve all employees |
| GET | `/employees/{id}` | Retrieve an employee by ID |
| POST | `/employees` | Create an employee |
| PUT | `/employees/{id}` | Update an employee |
| DELETE | `/employees/{id}` | Delete an employee |

See [API Documentation](docs/API_DOCUMENTATION.md) for request examples, validation rules, and error responses.

## Request Validation

The Employee API validates incoming request data before passing it to the service layer.

- Employee name is required and must not exceed 100 characters.
- Employee email is required, must have a valid email format, and must not exceed 150 characters.

Invalid request data returns HTTP 400 Bad Request.

Requests for nonexistent employees return HTTP 404 Not Found.

## Local Setup

### Prerequisites

- A compatible JDK for the Java version configured in the project
- Maven, or the Maven wrapper if included
- MySQL Server
- An API client such as Postman

### Setup Steps

1. Clone the repository.
2. Create a MySQL database for the application.
3. Configure the database URL, username, and password in the application's configuration.
4. Confirm the active Spring Boot and Java versions in `pom.xml`.
5. Run the application from your IDE or using Maven.
6. Test the Employee API using the endpoints documented in `docs/API_DOCUMENTATION.md`.

Do not commit real database passwords, API keys, or other credentials to GitHub.

## Testing

The Employee API can be tested with Postman.

Test scenarios should include:

- Successful employee creation and retrieval
- Successful employee update and deletion
- Invalid name and email values
- Missing required fields
- Requests for nonexistent employee IDs
- Verification that invalid requests do not modify persisted data

## Current Scope

The Employee module is the current implemented focus. Additional expense-management capabilities should be documented here only after they have been implemented and tested.

## Future Enhancements

Potential extensions include expense submission and approval workflows, employee-department relationships, authentication and authorization, and automated integration testing.

## Developer

Developed as a backend engineering project to demonstrate Java, Spring Boot, REST API design, persistence, validation, and exception handling.
