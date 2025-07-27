# VizfoliCraft Portfolio Generator Backend

A comprehensive Spring Boot application for generating, managing, and deploying portfolios with advanced admin capabilities and MongoDB integration.

## 🚀 Features

### Core Features
- **JWT-based Authentication** (Login/Register with role-based access)
- **User Profile Management** (Complete profile creation and sync)
- **Portfolio Template System** (CRUD operations with categorization)
- **Portfolio Deployment Service** (Deploy to multiple platforms with custom URLs)
- **Resume Generation** (PDF/HTML/DOCX with OpenHTMLToPDF)
- **Gamification System** (Badges, points, levels, and leaderboards)
- **Social Integration** (LinkedIn & GitHub profile sync)
- **Admin Panel** (Comprehensive admin dashboard with analytics)
- **Activity Logging** (Complete user activity tracking)
- **CSV Export** (Export users, activities, and deployments)

### Advanced Features
- **Multi-Platform Deployment** (Netlify, Vercel, AWS S3, GitHub Pages)
- **Custom Domain Support** (Custom subdomains and SSL)
- **SEO Optimization** (Meta tags, analytics integration)
- **Real-time Analytics** (View tracking and performance metrics)
- **Template Customization** (Custom CSS/JS injection)
- **Public Portfolio Gallery** (Showcase deployed portfolios)

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.4
- **Security**: Spring Security with JWT
- **Database**: MongoDB Atlas (Cloud NoSQL Database)
- **Build Tool**: Maven
- **Java Version**: 23
- **PDF Generation**: OpenHTMLToPDF 1.0.10
- **Template Engine**: Thymeleaf
- **Authentication**: JWT with BCrypt password encryption

## 📂 Project Structure

```
src/main/java/com/yourcompany/portfoliogenerator/
├── model/                          # MongoDB Document Models
│   ├── User.java                   # User entity with authentication
│   ├── UserProfile.java            # User profile information
│   ├── PortfolioTemplate.java      # Portfolio templates
│   ├── ResumeTemplate.java         # Resume templates
│   ├── UserTemplate.java           # User-selected templates
│   ├── DeployedPortfolio.java      # 🆕 Deployed portfolio tracking
│   ├── ActivityLog.java            # 🆕 Activity logging system
│   ├── Badge.java                  # Gamification badges
│   ├── UserBadge.java              # User earned badges
│   ├── UserStats.java              # User statistics
│   └── GeneratedResume.java        # Generated resume records
│
├── repository/                     # MongoDB Repositories
│   ├── UserRepository.java
│   ├── UserProfileRepository.java
│   ├── PortfolioTemplateRepository.java
│   ├── ResumeTemplateRepository.java
│   ├── UserTemplateRepository.java
│   ├── DeployedPortfolioRepository.java  # 🆕
│   ├── ActivityLogRepository.java       # 🆕
│   ├── BadgeRepository.java
│   ├── UserBadgeRepository.java
│   ├── UserStatsRepository.java
│   └── GeneratedResumeRepository.java
│
├── service/                        # Business Logic Services
│   ├── UserProfileService.java         # User profile management
│   ├── EnhancedTemplateService.java    # Template management
│   ├── GamificationService.java        # Badges and achievements
│   ├── ResumeGeneratorService.java     # Resume generation
│   ├── LinkedInIntegrationService.java # LinkedIn sync
│   ├── GitHubIntegrationService.java   # GitHub sync
│   ├── PortfolioDeploymentService.java # 🆕 Portfolio deployment
│   ├── PortfolioBuilderService.java    # 🆕 Portfolio building
│   ├── ActivityLogService.java         # 🆕 Activity logging
│   ├── AdminService.java               # 🆕 Admin operations
│   └── [DTOs and Request/Response classes]
│
├── controller/                     # REST API Controllers
│   ├── UserProfileController.java      # User profile APIs
│   ├── EnhancedTemplateController.java # Template APIs
│   ├── GamificationController.java     # Gamification APIs
│   ├── ResumeController.java           # Resume APIs
│   ├── PortfolioDeploymentController.java # 🆕 Deployment APIs
│   ├── AdminController.java            # 🆕 Admin panel APIs
│   └── publicsite/                     # Public facing controllers
│
├── config/                         # Configuration Classes
│   ├── SecurityConfig.java             # Security configuration
│   ├── ApplicationInitializationConfig.java # Startup initialization
│   └── [Other config classes]
│
└── ViztoufolicraftApplication.java # Main application class
```

## 🗄️ Database Setup

### MongoDB Atlas Configuration

1. **Create MongoDB Atlas Account**: Sign up at [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. **Create Cluster**: Set up a free cluster
3. **Configure Connection**: Update the connection string in `application.properties`

```properties
spring.data.mongodb.uri=mongodb+srv://username:password@cluster0.xxx.mongodb.net/portfolio_db
```

## 🚀 Getting Started

### Prerequisites

- **Java 23** or higher
- **Maven 3.6+**
- **MongoDB Atlas Account** (or local MongoDB)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/vizahat36/VizfoliCraft.git
cd VizfoliCraft/backend/viztoufolicraft
```

2. **Configure MongoDB connection**
Update `src/main/resources/application.properties` with your MongoDB Atlas URI

3. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📡 Complete API Endpoints

### 🔐 Authentication APIs
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

### 👤 User Profile APIs
- `GET /api/users/profile` - Get user profile
- `POST /api/users/profile` - Create/update profile
- `PUT /api/users/profile` - Update profile
- `DELETE /api/users/profile` - Delete profile
- `PUT /api/users/profile/sync/{platform}` - Update sync status

### 📄 Template Management APIs
- `GET /api/templates/portfolio` - List portfolio templates
- `GET /api/templates/portfolio/category/{category}` - Templates by category
- `GET /api/templates/portfolio/free` - Free templates
- `GET /api/templates/portfolio/premium` - Premium templates
- `POST /api/templates/portfolio` - Create template
- `PUT /api/templates/portfolio/{id}` - Update template
- `DELETE /api/templates/portfolio/{id}` - Delete template

### 🚀 Portfolio Deployment APIs
- `GET /api/portfolio/templates` - List all available templates
- `GET /api/portfolio/templates/category/{category}` - Templates by category
- `GET /api/portfolio/templates/free` - Free templates
- `GET /api/portfolio/templates/premium` - Premium templates
- `GET /api/portfolio/templates/{templateId}` - Get specific template
- `POST /api/portfolio/deploy/{templateId}` - **Deploy portfolio with template**
- `PUT /api/portfolio/deploy/{deploymentId}` - Update deployment
- `DELETE /api/portfolio/deploy/{deploymentId}` - Delete deployment
- `GET /api/portfolio/deployments` - Get user's deployments
- `GET /api/portfolio/deployments/{deploymentId}` - Get specific deployment
- `GET /api/portfolio/public` - Get public portfolios
- `POST /api/portfolio/view/{deploymentId}` - Increment view count

### 📄 Resume Generation APIs
- `GET /api/resume/templates` - List resume templates
- `POST /api/resume/generate` - Generate resume
- `GET /api/resume/generated` - List generated resumes
- `GET /api/resume/generated/{id}` - Get specific resume
- `DELETE /api/resume/generated/{id}` - Delete generated resume

### 🏆 Gamification APIs
- `GET /api/gamification/badges` - Available badges
- `GET /api/gamification/user-badges` - User's badges
- `GET /api/gamification/stats` - User statistics
- `GET /api/gamification/leaderboard` - Top users leaderboard
- `POST /api/gamification/check-achievements` - Check for new achievements

### 🛠️ Admin Panel APIs (ADMIN ROLE REQUIRED)
- `GET /api/admin/dashboard` - **Dashboard statistics**
- `GET /api/admin/users` - List all users (paginated)
- `GET /api/admin/users/search?query=` - Search users
- `PUT /api/admin/users/{userId}/role?role=` - Update user role
- `PUT /api/admin/users/{userId}/toggle-status` - Enable/disable user
- `GET /api/admin/activities` - All activity logs
- `GET /api/admin/activities/type/{type}` - Activities by type
- `GET /api/admin/activities/date-range` - Activities by date range
- `GET /api/admin/deployments` - All deployments
- `GET /api/admin/badges` - All badges
- `POST /api/admin/badges` - Create new badge
- `PUT /api/admin/badges/{badgeId}` - Update badge
- `DELETE /api/admin/badges/{badgeId}` - Delete badge
- `GET /api/admin/leaderboard?limit=` - User leaderboard
- `GET /api/admin/export/users` - **Export users CSV**
- `GET /api/admin/export/activities` - **Export activities CSV**
- `GET /api/admin/export/deployments` - **Export deployments CSV**

## 🔐 Authentication

The API uses JWT tokens for authentication. Include the token in the Authorization header:

```bash
Authorization: Bearer <your-jwt-token>
```

### Roles
- **USER**: Default role for regular users
- **ADMIN**: Administrative access to all admin panel features

## 💡 Sample API Requests

### Register New User
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

### Login User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### Deploy Portfolio
```bash
curl -X POST http://localhost:8080/api/portfolio/deploy/TEMPLATE_ID \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "subdomain": "johndoe",
    "title": "John Doe Portfolio",
    "description": "Full Stack Developer Portfolio",
    "isPublic": true,
    "customCSS": ".custom { color: blue; }"
  }'
```

### Get Dashboard Stats (Admin)
```bash
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer <admin-jwt-token>"
```

### Export Users CSV (Admin)
```bash
curl -X GET http://localhost:8080/api/admin/export/users \
  -H "Authorization: Bearer <admin-jwt-token>" \
  -o users.csv
```

## ⚙️ Configuration

### Key Properties in `application.properties`:

```properties
# MongoDB Atlas Configuration
spring.data.mongodb.uri=mongodb+srv://user:pass@cluster.mongodb.net/portfolio_db

# JWT Configuration
jwt.secret=your-super-secret-key-here
jwt.expiration=86400

# Portfolio Deployment
app.deployment.base-url=https://portfolios.vizfolicraft.com
app.deployment.default-platform=INTERNAL_CDN

# Resume Generation
app.resume.storage.path=./resumes
app.resume.base-url=http://localhost:8080

# CORS Configuration
app.cors.allowed-origins=http://localhost:3000,http://localhost:4200

# Integration APIs
app.integration.github.api-url=https://api.github.com
app.integration.linkedin.api-url=https://api.linkedin.com/v2
```

## 🔧 Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
```

### Building Docker Image
```bash
docker build -t vizfolicraft-backend .
```

## 🌟 Key Features Deep Dive

### 1. Portfolio Deployment System
- **Multi-platform deployment** to Netlify, Vercel, AWS S3, GitHub Pages
- **Custom subdomain generation** with conflict resolution
- **SEO optimization** with meta tags and analytics
- **Real-time deployment status** tracking
- **Custom CSS/JS injection** for personalization

### 2. Admin Panel Dashboard
- **Real-time statistics** of users, deployments, activities
- **User management** with role assignment and status control
- **Activity monitoring** with detailed logging and filtering
- **CSV export** capabilities for data analysis
- **Badge management** for gamification system

### 3. Gamification System
- **Dynamic badge system** with point requirements
- **User statistics** tracking and leaderboards
- **Achievement detection** with automated awarding
- **Experience points** and level progression
- **Social engagement** features

### 4. Resume Generation
- **Multiple format support** (PDF, HTML, DOCX)
- **Template-based generation** with customization
- **Automatic cleanup** of generated files
- **Integration** with user profile data

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue on GitHub
- Contact: vizahat36@gmail.com

## 🔄 Changelog

### Version 2.0.0 (Latest)
- ✅ **Added**: Complete portfolio deployment system
- ✅ **Added**: Comprehensive admin panel with analytics
- ✅ **Added**: Activity logging and monitoring
- ✅ **Added**: CSV export capabilities
- ✅ **Migrated**: From PostgreSQL to MongoDB Atlas
- ✅ **Enhanced**: Template management system
- ✅ **Added**: Multi-platform deployment support

### Version 1.0.0
- ✅ Basic portfolio template management
- ✅ JWT authentication system
- ✅ User profile management
- ✅ PostgreSQL integration

---

**Built with ❤️ by VizfoliCraft Team**
