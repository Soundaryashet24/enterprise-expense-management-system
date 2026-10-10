# enterprise-expense-management-system
A backend application for managing employee expenses, approvals, reimbursements, and expense workflows using Java and Spring Boot.

### Architecture

Client ->  REST Controller ->  Request / Response DTO ->  Service -> Repository -> MySQL
  
### Error Handling Flow

Controller -> Service -> EmployeeNotFoundException -> GlobalExceptionHandler -> HTTP 404 NOT FOUND
   

              