# Portfolio Generator Backend

A Spring Boot application for generating and managing portfolios with JWT authentication.

## Features

- JWT-based Authentication (Login/Register)
- User Management with role-based access control
- Portfolio Template Management (CRUD operations)
- Template Selection and Customization
- Portfolio Deployment with unique URLs
- Portfolio Template Gallery
- Admin Panel
- RESTful API endpoints
- PostgreSQL Database integration
- Password encryption with BCrypt

## Tech Stack

- **Framework**: Spring Boot 3.5.4
- **Security**: Spring Security with JWT
- **Database**: PostgreSQL with JPA/Hibernate
- **Build Tool**: Maven
- **Java Version**: 17

## Project Structure

```
src/
└── main/
    ├── java/
    │   └── com/
    │       └── yourcompany/
    │           └── portfoliogenerator/
    │               ├── admin/        # Admin panel controllers
    │               ├── publicsite/   # Public website controllers
    │               ├── config/       # Security, JWT, CORS configuration
    │               ├── model/        # Entity classes (User, Portfolio, etc.)
    │               ├── repository/   # JPA repositories
    │               ├── service/      # Business logic and DTOs
    │               └── PortfolioGeneratorApplication.java
    └── resources/
        ├── application.properties
        ├── static/
        └── templates/
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Database Setup

1. Install PostgreSQL
2. Create a database named `portfolio_db`
3. Update the database credentials in `application.properties`

### Installation

1. Clone the repository
2. Configure database settings in `src/main/resources/application.properties`
3. Run the application:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user

### Templates (Public Access)
- `GET /api/templates` - Get all active templates
- `GET /api/templates/featured` - Get featured templates
- `GET /api/templates/types` - Get available template types
- `GET /api/templates/type/{type}` - Get templates by type
- `GET /api/templates/search?keyword={keyword}` - Search templates
- `GET /api/templates/{id}` - Get template details

### Template Management (Authenticated)
- `POST /api/templates` - Create new template
- `PUT /api/templates/{id}` - Update template
- `DELETE /api/templates/{id}` - Delete template
- `GET /api/templates/my-templates` - Get user's templates

### User Templates (Authenticated)
- `GET /api/user-templates` - Get user's selected templates
- `GET /api/user-templates/deployed` - Get deployed templates
- `POST /api/user-templates/select/{templateId}` - Select a template
- `GET /api/user-templates/{id}` - Get user template details
- `PUT /api/user-templates/{id}` - Update user template
- `POST /api/user-templates/{id}/deploy` - Deploy template
- `POST /api/user-templates/{id}/undeploy` - Undeploy template
- `DELETE /api/user-templates/{id}` - Delete user template

### Portfolio Rendering (Public Access)
- `GET /portfolio/{username}/{portfolioId}` - View deployed portfolio
- `GET /portfolio/{username}/{portfolioId}/preview` - Preview portfolio

### Public
- `GET /api/public/portfolios` - Get all published portfolios
- `GET /api/public/portfolios/{id}` - Get a specific portfolio

### Admin (Requires ADMIN role)
- `GET /api/admin/users` - Get all users
- `GET /api/admin/users/{id}` - Get user by ID

## Authentication

The API uses JWT tokens for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Sample Requests

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### Get All Templates
```bash
curl -X GET http://localhost:8080/api/templates
```

### Select a Template
```bash
curl -X POST http://localhost:8080/api/user-templates/select/1 \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Deploy a Portfolio
```bash
curl -X POST http://localhost:8080/api/user-templates/1/deploy \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Configuration

Key configuration properties in `application.properties`:

- `jwt.secret` - JWT signing secret
- `jwt.expiration` - Token expiration time in seconds
- Database connection settings
- Server port configuration

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
