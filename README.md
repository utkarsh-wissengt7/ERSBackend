# Expense Reimbursement System (ERS) Backend

A robust Spring Boot application that handles expense reimbursement workflows with features like user management, expense tracking, and automated notifications.

## 🚀 Features

- **User Management**
  - Role-based authentication (Admin, Manager, Employee)
  - JWT-based secure authentication
  - User activation/deactivation
  - Manager-reportee relationship management

- **Expense Management**
  - Create, read, update, and delete expenses
  - File attachments with Cloudinary integration
  - Expense approval workflow
  - Status tracking (Pending, Approved, Rejected)

- **Notification System**
  - Email notifications for expense submissions
  - Status update notifications
  - HTML email templates
  - Automated manager notifications

- **Security**
  - JWT Authentication
  - Password encryption with BCrypt
  - CORS configuration
  - Stateless session management

## 🛠 Tech Stack

- Java 21
- Spring Boot 3.4.3
- Spring Security
- PostgreSQL
- Gradle
- JUnit 5 & Mockito
- Cloudinary
- JavaMail
- JWT
- Lombok
- Swagger/OpenAPI

## 📊 API Endpoints

### User Management
```
POST   /api/users/authenticate      # User login
POST   /api/users                  # Create new user
GET    /api/users                  # Get all users
GET    /api/users/{wissenID}       # Get user by ID
PUT    /api/users/{wissenID}       # Update user
PUT    /api/users/toggle-status/{wissenID} # Toggle user status
```

### Expense Management
```
GET    /api/expenses/user          # Get user's expenses
POST   /api/expenses              # Create expense
PUT    /api/expenses/{id}         # Update expense
DELETE /api/expenses/{id}         # Delete expense
PUT    /api/expenses/{id}/status/{action} # Update expense status
```

### Notifications
```
GET    /api/notifications         # Get all notifications
GET    /api/notifications/user/{userId} # Get user notifications
POST   /api/notifications        # Create notification
```

### File Upload
```
POST   /api/upload/pdf           # Upload expense receipt
```

## 🔧 Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Email Configuration
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Security Configuration
```properties
jwt.secret=your-jwt-secret-key
```

## 🚀 Getting Started

1. **Prerequisites**
   - JDK 21
   - PostgreSQL
   - Gradle

2. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/ERSBackend.git
   ```

3. **Configure application.properties**
   - Update database credentials
   - Configure email settings
   - Set JWT secret

4. **Build the project**
   ```bash
   ./gradlew clean build
   ```

5. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

## 🧪 Testing

The project includes comprehensive unit tests for controllers, services, and utilities.

Run tests with:
```bash
./gradlew test
```

Generate test coverage report:
```bash
./gradlew jacocoTestReport
```

## 🐳 Docker Support

Build Docker image:
```bash
docker build -t ers-backend .
```

Run container:
```bash
docker run -p 8081:8081 ers-backend
```

## 🔄 CI/CD

The project includes a Jenkins pipeline that:
- Builds the application
- Runs tests
- Generates test coverage reports
- Performs SonarQube analysis
- Builds Docker image

## 🔒 Security Features

- JWT-based authentication
- Password encryption
- CORS protection
- Role-based access control
- Secure email notifications
- Input validation
- Exception handling

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── config/
│   │       ├── controllers/
│   │       ├── dto/
│   │       ├── exceptions/
│   │       ├── filters/
│   │       ├── models/
│   │       ├── repositories/
│   │       ├── services/
│   │       └── utils/
│   └── resources/
│       ├── templates/
│       └── application.properties
└── test/
    └── java/
        └── com/example/demo/
            ├── controllers/
            ├── services/
            └── utils/
```

## 📝 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
