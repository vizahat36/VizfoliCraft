# VizfoliCraft - Portfolio Generator Platform

[![Project Status](https://img.shields.io/badge/Status-Backend%20Complete-green)]()
[![Frontend Progress](https://img.shields.io/badge/Frontend-15%25-yellow)]()
[![Testing](https://img.shields.io/badge/Testing-0%25-red)]()
[![License](https://img.shields.io/badge/License-MIT-blue.svg)]()

> **A comprehensive, full-stack portfolio generator platform with advanced deployment, resume generation, and gamification features.**

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Status](#project-status)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [Development Roadmap](#development-roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

**VizfoliCraft** is a modern portfolio generator platform that enables users to create, customize, and deploy professional portfolios and resumes with ease. The platform features a powerful Spring Boot backend with MongoDB and a React-based frontend (in development).

### Key Highlights

- 🔐 **Secure Authentication**: JWT-based authentication with role-based access control
- 📄 **Portfolio Templates**: Multiple professional templates with full customization
- 🚀 **Multi-Platform Deployment**: Deploy to Netlify, Vercel, AWS S3, GitHub Pages, or Internal CDN
- 📝 **Resume Generation**: Generate resumes in PDF, HTML, and DOCX formats
- 🏆 **Gamification**: Badges, points, levels, and leaderboards to engage users
- 🛠️ **Admin Panel**: Comprehensive dashboard with analytics and management tools
- 🔗 **Social Integration**: Sync with LinkedIn and GitHub profiles

---

## ✨ Features

### For Users
- ✅ Create and manage professional portfolios
- ✅ Choose from multiple portfolio templates
- ✅ Customize templates with custom CSS/JS
- ✅ Deploy portfolios to multiple platforms
- ✅ Generate professional resumes in multiple formats
- ✅ Track portfolio views and analytics
- ✅ Earn badges and climb leaderboards
- ✅ Sync data from LinkedIn and GitHub

### For Administrators
- ✅ Comprehensive dashboard with real-time statistics
- ✅ User management (search, role assignment, enable/disable)
- ✅ Activity monitoring and logging
- ✅ Deployment tracking across all platforms
- ✅ Badge management for gamification
- ✅ CSV export of users, activities, and deployments
- ✅ Advanced analytics and reporting

---

## 📊 Project Status

**Overall Completion**: ~65%

| Component | Status | Progress |
|-----------|--------|----------|
| **Backend API** | ✅ Complete | 100% |
| **Database Design** | ✅ Complete | 100% |
| **Documentation** | ✅ Nearly Complete | 90% |
| **Frontend Structure** | 🟡 In Progress | 15% |
| **Frontend Implementation** | ❌ Pending | 5% |
| **Backend Testing** | ❌ Pending | 0% |
| **Frontend Testing** | ❌ Pending | 0% |
| **Deployment Setup** | ❌ Pending | 0% |

📄 **For detailed progress tracking**: See [PROGRESS.md](PROGRESS.md)  
📊 **For quick status overview**: See [PROJECT_STATUS.md](PROJECT_STATUS.md)

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 23
- **Database**: MongoDB Atlas (Cloud NoSQL)
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **PDF Generation**: OpenHTMLToPDF 1.0.10
- **Template Engine**: Thymeleaf

### Frontend
- **Framework**: React 18
- **Build Tool**: Create React App
- **State Management**: React Context API
- **Styling**: CSS3 (custom themes)
- **HTTP Client**: Axios (planned)
- **Routing**: React Router (planned)

### Infrastructure
- **Database Hosting**: MongoDB Atlas
- **Deployment Platforms**: Netlify, Vercel, AWS S3, GitHub Pages, Internal CDN
- **Authentication**: JWT tokens
- **File Storage**: Local storage (configurable)

---

## 🚀 Getting Started

### Prerequisites

- **Java 23** or higher
- **Maven 3.6+**
- **Node.js 18+** and **npm 9+**
- **MongoDB Atlas Account** (or local MongoDB)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/vizahat36/VizfoliCraft.git
   cd VizfoliCraft/backend/viztoufolicraft
   ```

2. **Configure MongoDB**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/portfolio_db
   jwt.secret=your-super-secret-key-here
   ```

3. **Run the backend**
   ```bash
   mvn spring-boot:run
   ```
   
   Backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend/portfolio-generator
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm start
   ```
   
   Frontend will start on `http://localhost:3000`

   > ⚠️ **Note**: Frontend is currently in development with limited functionality

---

## 📁 Project Structure

```
VizfoliCraft/
├── backend/
│   └── viztoufolicraft/              # Spring Boot Backend
│       ├── src/main/java/com/yourcompany/portfoliogenerator/
│       │   ├── model/                # Data Models (15+ entities)
│       │   ├── repository/           # MongoDB Repositories
│       │   ├── service/              # Business Logic Services
│       │   ├── controller/           # REST API Controllers
│       │   ├── config/               # Configuration Classes
│       │   └── admin/                # Admin Panel Controllers
│       ├── src/main/resources/
│       │   ├── application.properties
│       │   └── templates/            # Portfolio & Resume Templates
│       ├── README.md                 # Backend Documentation
│       └── API_DOCUMENTATION.md      # Complete API Reference
│
├── frontend/
│   └── portfolio-generator/          # React Frontend
│       ├── src/
│       │   ├── components/           # Reusable Components
│       │   ├── contexts/             # React Contexts
│       │   ├── services/             # API Services
│       │   ├── publicSite/           # Public Pages
│       │   ├── adminPanel/           # Admin Pages
│       │   ├── templates/            # Portfolio Templates
│       │   ├── theme/                # Theme Configuration
│       │   └── utils/                # Utility Functions
│       ├── package.json
│       └── README.md                 # Frontend Documentation
│
├── PROGRESS.md                        # Detailed Progress Tracking
├── PROJECT_STATUS.md                  # Quick Status Summary
└── README.md                          # This File
```

---

## 📚 Documentation

### Available Documentation

1. **[PROGRESS.md](PROGRESS.md)** - Detailed progress tracking with completed features, pending work, and roadmap
2. **[PROJECT_STATUS.md](PROJECT_STATUS.md)** - Quick project status summary and next steps
3. **[FRONTEND_GUIDE.md](FRONTEND_GUIDE.md)** - **NEW!** Complete frontend development guide with API integration
4. **[Backend README](backend/viztoufolicraft/README.md)** - Backend setup, features, and configuration
5. **[API Documentation](backend/viztoufolicraft/API_DOCUMENTATION.md)** - Complete API reference with examples
6. **[Frontend README](frontend/portfolio-generator/README.md)** - Frontend setup and available scripts

### API Documentation Highlights

The backend provides **50+ REST API endpoints** including:

- **Authentication**: `/api/auth/register`, `/api/auth/login`
- **User Profiles**: `/api/users/profile/*`
- **Portfolio (Simplified)**: `/api/portfolio/create`, `/api/portfolio/me`, `/api/portfolio/update`, `/api/portfolio/publish` **NEW!**
- **Templates**: `/api/portfolio/templates/*`
- **Deployment (Advanced)**: `/api/portfolio/deploy/*`
- **Resume**: `/api/resume/*`
- **Gamification**: `/api/gamification/*`
- **Admin Panel**: `/api/admin/*`

See [API_DOCUMENTATION.md](backend/viztoufolicraft/API_DOCUMENTATION.md) for complete details.

### Frontend Development Guide

**NEW!** Check out [FRONTEND_GUIDE.md](FRONTEND_GUIDE.md) for:
- Complete frontend roadmap and architecture
- Page-by-page implementation guide
- API integration examples with code snippets
- Authentication flow with React Context
- Step-by-step development checklist

---

## 🗺️ Development Roadmap

### ✅ Phase 1: Backend Development (COMPLETE)
- [x] Database design and models
- [x] Authentication and authorization
- [x] User management system
- [x] Template management
- [x] Portfolio deployment service
- [x] Resume generation
- [x] Gamification system
- [x] Admin panel APIs
- [x] Activity logging
- [x] Social integration
- [x] API documentation

### 🟡 Phase 2: Frontend Development (IN PROGRESS - 15%)
- [x] Project structure setup
- [x] Component architecture
- [ ] Authentication UI (Login/Signup)
- [ ] User dashboard
- [ ] Template gallery
- [ ] Portfolio customization interface
- [ ] Resume generation UI
- [ ] Admin panel UI
- [ ] Responsive design
- [ ] Theme implementation

### ❌ Phase 3: Testing (PENDING)
- [ ] Backend unit tests
- [ ] Backend integration tests
- [ ] Frontend unit tests
- [ ] Frontend component tests
- [ ] E2E tests
- [ ] Performance tests
- [ ] Security tests

### ❌ Phase 4: Deployment (PENDING)
- [ ] CI/CD pipeline
- [ ] Docker containerization
- [ ] Backend deployment
- [ ] Frontend deployment
- [ ] Domain and SSL setup
- [ ] Monitoring and logging
- [ ] Backup strategy

### ❌ Phase 5: Enhancement (FUTURE)
- [ ] Payment integration
- [ ] Advanced analytics
- [ ] Multi-language support
- [ ] Email notifications
- [ ] Mobile app
- [ ] Team collaboration features

---

## 🎯 Next Milestones

### Immediate (Weeks 1-2)
1. Implement authentication UI
2. Create user dashboard
3. Build template gallery
4. Start backend testing

### Short-term (Weeks 3-6)
1. Complete portfolio creation flow
2. Implement admin panel UI
3. Achieve 70% backend test coverage
4. Complete core user features

### Medium-term (Weeks 7-12)
1. Complete frontend implementation
2. Full test coverage
3. Production deployment
4. Launch MVP

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Current Needs

**High Priority:**
- Frontend developers to implement React UI
- QA engineers to write tests
- DevOps engineers for deployment setup

**Medium Priority:**
- UI/UX designers for design improvements
- Technical writers for documentation
- Security experts for security review

### How to Contribute

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow existing code structure and style
- Write tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

---

## 📊 Statistics

### Current Codebase

- **Java Classes**: 80+
- **REST Endpoints**: 50+
- **Database Models**: 15+
- **React Components**: 30+ (structure only)
- **Services**: 12+ (backend only)
- **Lines of Code**: 25,000+ (backend)

### Test Coverage

- Backend: 0% (pending)
- Frontend: 0% (pending)
- **Goal**: 70%+ coverage

---

## 🐛 Known Issues

1. Frontend implementation pending
2. No test coverage yet
3. Deployment configuration needed
4. Some empty service files in backend

See [PROGRESS.md](PROGRESS.md) for detailed issues and recommendations.

---

## 📞 Support & Contact

- **Email**: vizahat36@gmail.com
- **GitHub Issues**: [Create an Issue](https://github.com/vizahat36/VizfoliCraft/issues)
- **Repository**: [VizfoliCraft on GitHub](https://github.com/vizahat36/VizfoliCraft)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Spring Boot community for excellent framework
- MongoDB Atlas for cloud database hosting
- React community for frontend framework
- OpenHTMLToPDF for PDF generation
- All contributors and supporters

---

## 🌟 Show Your Support

If you find this project useful, please consider:
- ⭐ Starring the repository
- 🐛 Reporting bugs
- 💡 Suggesting new features
- 🤝 Contributing code
- 📢 Sharing with others

---

**Built with ❤️ by the VizfoliCraft Team**

---

*Last Updated: November 24, 2025*  
*Version: 1.0.0-alpha*  
*Status: Backend Complete, Frontend & Testing in Progress*
