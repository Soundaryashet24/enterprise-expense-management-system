# enterprise-expense-management-system
A backend application for managing employee expenses, approvals, reimbursements, and expense workflows using Java and Spring Boot.

### Architecture

Client ->  REST Controller ->  Request / Response DTO ->  Service -> Repository -> MySQL

   
### Request Validation and Error Handling Flow

The Employee API uses Jakarta Bean Validation to validate incoming request data before it reaches the service layer.

**Validation rules**
- Employee name is required and cannot contain only whitespace.
- Employee name cannot exceed 100 characters.
- Employee email is required and must follow a valid email format.
- Employee email cannot exceed 150 characters.

**Error handling**

Controller -> Service -> EmployeeNotFoundException -> GlobalExceptionHandler -> HTTP 404 NOT FOUND

Invalid request data returns `400 Bad Request` with field-level validation messages. Requests that reference a non-existent employee return `404 Not Found`, provided the global exception handler is configured.

**Example invalid request**

```json
{
  "name": "",
  "email": "invalid-email"
}
```

**Example error response**

```json
{
  "name": "Employee name is required",
  "email": "Please provide a valid email address"
}
```

Validation is applied to employee creation and update requests using `@Valid` on the request DTO.

              
