# VizfoliCraft API Documentation

Complete API documentation for VizfoliCraft Portfolio Generator Backend.

## 📋 Table of Contents

- [Authentication](#authentication)
- [User Profile Management](#user-profile-management)
- [Template Management](#template-management)
- [Portfolio Deployment](#portfolio-deployment)
- [Resume Generation](#resume-generation)
- [Gamification System](#gamification-system)
- [Admin Panel](#admin-panel)
- [Response Formats](#response-formats)
- [Error Handling](#error-handling)

---

## 🔐 Authentication

### Register New User
**POST** `/api/auth/register`

```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "id": "64f8a1234567890abcdef123",
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER",
  "createdAt": "2025-01-27T10:30:00"
}
```

### Login User
**POST** `/api/auth/login`

```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": "64f8a1234567890abcdef123",
    "username": "john_doe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

---

## 👤 User Profile Management

### Get User Profile
**GET** `/api/users/profile`
*Requires: Authentication*

**Response:**
```json
{
  "id": "64f8a1234567890abcdef123",
  "displayName": "John Doe",
  "profession": "Full Stack Developer",
  "location": "New York, USA",
  "bio": "Passionate developer with 5 years of experience...",
  "profileImageUrl": "https://example.com/profile.jpg",
  "phoneNumber": "+1-234-567-8900",
  "website": "https://johndoe.dev",
  "linkedinUrl": "https://linkedin.com/in/johndoe",
  "githubUrl": "https://github.com/johndoe",
  "skills": "JavaScript, React, Node.js, Python",
  "yearsOfExperience": 5,
  "availableForHire": true,
  "createdAt": "2025-01-27T10:30:00",
  "updatedAt": "2025-01-27T12:45:00"
}
```

### Create/Update Profile
**POST** `/api/users/profile`
*Requires: Authentication*

```json
{
  "displayName": "John Doe",
  "profession": "Full Stack Developer",
  "location": "New York, USA",
  "bio": "Passionate developer...",
  "phoneNumber": "+1-234-567-8900",
  "website": "https://johndoe.dev",
  "skills": "JavaScript, React, Node.js",
  "yearsOfExperience": 5,
  "availableForHire": true
}
```

---

## 📄 Template Management

### List Portfolio Templates
**GET** `/api/portfolio/templates`

**Query Parameters:**
- `category` (optional): Filter by category
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response:**
```json
{
  "content": [
    {
      "id": "64f8a1234567890abcdef456",
      "name": "Modern Portfolio",
      "description": "Clean and modern portfolio template",
      "category": "Professional",
      "previewImageUrl": "https://example.com/preview.jpg",
      "isPremium": false,
      "isActive": true,
      "createdAt": "2025-01-20T09:00:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 2,
  "number": 0,
  "size": 20
}
```

### Get Template by ID
**GET** `/api/portfolio/templates/{templateId}`

**Response:**
```json
{
  "id": "64f8a1234567890abcdef456",
  "name": "Modern Portfolio",
  "description": "Clean and modern portfolio template",
  "category": "Professional",
  "htmlContent": "<!DOCTYPE html>...",
  "cssContent": ".container { ... }",
  "jsContent": "function init() { ... }",
  "previewImageUrl": "https://example.com/preview.jpg",
  "isPremium": false,
  "isActive": true,
  "createdAt": "2025-01-20T09:00:00"
}
```

---

## 🚀 Portfolio Deployment

### Deploy Portfolio
**POST** `/api/portfolio/deploy/{templateId}`
*Requires: Authentication*

```json
{
  "subdomain": "johndoe",
  "customDomain": "johndoe.com",
  "title": "John Doe - Full Stack Developer",
  "description": "Professional portfolio showcasing my work",
  "metaTitle": "John Doe Portfolio",
  "metaDescription": "Full Stack Developer Portfolio",
  "metaKeywords": "developer, portfolio, javascript, react",
  "customCSS": ".custom-style { color: #0066cc; }",
  "customJS": "console.log('Portfolio loaded');",
  "isPublic": true,
  "passwordProtected": false,
  "allowMultiple": false
}
```

**Response:**
```json
{
  "id": "64f8a1234567890abcdef789",
  "deploymentId": "deploy-uuid-123",
  "publicUrl": "https://johndoe.vizfolicraft.com",
  "subdomain": "johndoe",
  "title": "John Doe - Full Stack Developer",
  "status": "PENDING",
  "platform": "INTERNAL_CDN",
  "isActive": true,
  "isPublic": true,
  "viewCount": 0,
  "createdAt": "2025-01-27T14:30:00"
}
```

### Get User Deployments
**GET** `/api/portfolio/deployments`
*Requires: Authentication*

**Response:**
```json
[
  {
    "id": "64f8a1234567890abcdef789",
    "publicUrl": "https://johndoe.vizfolicraft.com",
    "title": "John Doe Portfolio",
    "status": "DEPLOYED",
    "viewCount": 156,
    "deployedAt": "2025-01-27T14:35:00",
    "lastViewed": "2025-01-27T16:22:00"
  }
]
```

### Update Deployment
**PUT** `/api/portfolio/deploy/{deploymentId}`
*Requires: Authentication*

```json
{
  "title": "Updated Portfolio Title",
  "description": "Updated description",
  "customCSS": ".new-style { ... }",
  "isPublic": true
}
```

### Delete Deployment
**DELETE** `/api/portfolio/deploy/{deploymentId}`
*Requires: Authentication*

**Response:** `200 OK`

### Increment View Count
**POST** `/api/portfolio/view/{deploymentId}`

**Response:** `200 OK`

---

## 📄 Resume Generation

### List Resume Templates
**GET** `/api/resume/templates`
*Requires: Authentication*

**Response:**
```json
[
  {
    "id": "64f8a1234567890abcdef321",
    "name": "Professional Resume",
    "description": "Clean professional resume template",
    "templateType": "PROFESSIONAL",
    "previewImageUrl": "https://example.com/resume-preview.jpg",
    "isPremium": false
  }
]
```

### Generate Resume
**POST** `/api/resume/generate`
*Requires: Authentication*

```json
{
  "templateId": "64f8a1234567890abcdef321",
  "format": "PDF",
  "customizations": {
    "primaryColor": "#0066cc",
    "fontSize": "12px",
    "includePhoto": true
  }
}
```

**Response:**
```json
{
  "id": "64f8a1234567890abcdef654",
  "fileName": "john_doe_resume.pdf",
  "fileUrl": "http://localhost:8080/resumes/john_doe_resume.pdf",
  "format": "PDF",
  "status": "COMPLETED",
  "generatedAt": "2025-01-27T15:30:00"
}
```

---

## 🏆 Gamification System

### Get Available Badges
**GET** `/api/gamification/badges`

**Response:**
```json
[
  {
    "id": "64f8a1234567890abcdef987",
    "name": "First Portfolio",
    "description": "Created your first portfolio",
    "iconUrl": "https://example.com/badge1.png",
    "category": "Achievement",
    "pointsRequired": 10,
    "isActive": true
  }
]
```

### Get User Stats
**GET** `/api/gamification/stats`
*Requires: Authentication*

**Response:**
```json
{
  "totalPoints": 150,
  "currentLevel": 3,
  "portfoliosCreated": 2,
  "resumesGenerated": 5,
  "socialAccountsConnected": 2,
  "profileCompletionPercentage": 85,
  "currentStreak": 7,
  "longestStreak": 12,
  "lastActivityDate": "2025-01-27T16:00:00"
}
```

### Get User Badges
**GET** `/api/gamification/user-badges`
*Requires: Authentication*

**Response:**
```json
[
  {
    "id": "64f8a1234567890abcdef111",
    "badge": {
      "name": "First Portfolio",
      "description": "Created your first portfolio",
      "iconUrl": "https://example.com/badge1.png"
    },
    "earnedAt": "2025-01-25T10:30:00",
    "pointsEarned": 10
  }
]
```

### Get Leaderboard
**GET** `/api/gamification/leaderboard`

**Query Parameters:**
- `limit` (optional): Number of users to return (default: 10)

**Response:**
```json
[
  {
    "rank": 1,
    "username": "john_doe",
    "totalPoints": 450,
    "currentLevel": 8,
    "badgeCount": 12
  },
  {
    "rank": 2,
    "username": "jane_smith",
    "totalPoints": 380,
    "currentLevel": 7,
    "badgeCount": 10
  }
]
```

---

## 🛠️ Admin Panel APIs

*All admin endpoints require ADMIN role*

### Get Dashboard Statistics
**GET** `/api/admin/dashboard`

**Response:**
```json
{
  "totalUsers": 1250,
  "activeUsers": 1180,
  "totalDeployments": 2340,
  "totalTemplates": 45,
  "totalBadges": 25,
  "recentUsers": 15,
  "recentDeployments": 23,
  "recentActivities": 156,
  "topActivityTypes": {
    "PORTFOLIO_DEPLOYMENT": 45,
    "RESUME_GENERATION": 32,
    "PROFILE_UPDATE": 28
  },
  "platformStats": {
    "INTERNAL_CDN": 1200,
    "NETLIFY": 800,
    "VERCEL": 340
  },
  "lastUpdated": "2025-01-27T16:30:00"
}
```

### List All Users
**GET** `/api/admin/users`

**Query Parameters:**
- `page` (optional): Page number
- `size` (optional): Page size
- `sort` (optional): Sort field and direction

**Response:**
```json
{
  "content": [
    {
      "id": "64f8a1234567890abcdef123",
      "username": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER",
      "enabled": true,
      "createdAt": "2025-01-20T10:30:00"
    }
  ],
  "totalElements": 1250,
  "totalPages": 63,
  "number": 0,
  "size": 20
}
```

### Search Users
**GET** `/api/admin/users/search?query=john`

**Response:** Same format as List All Users

### Update User Role
**PUT** `/api/admin/users/{userId}/role?role=ADMIN`

**Response:** `200 OK`

### Toggle User Status
**PUT** `/api/admin/users/{userId}/toggle-status`

**Response:** `200 OK`

### Get Activity Logs
**GET** `/api/admin/activities`

**Query Parameters:**
- `page`, `size`, `sort`: Pagination parameters
- `type` (optional): Filter by activity type
- `startDate`, `endDate` (optional): Date range filter

**Response:**
```json
{
  "content": [
    {
      "id": "64f8a1234567890abcdef555",
      "user": {
        "username": "john_doe",
        "email": "john@example.com"
      },
      "action": "PORTFOLIO_DEPLOYMENT",
      "description": "Deployed portfolio successfully",
      "type": "PORTFOLIO_DEPLOYMENT",
      "ipAddress": "192.168.1.100",
      "timestamp": "2025-01-27T14:30:00"
    }
  ],
  "totalElements": 5670,
  "totalPages": 284,
  "number": 0,
  "size": 20
}
```

### Badge Management

#### Create Badge
**POST** `/api/admin/badges`

```json
{
  "name": "Portfolio Master",
  "description": "Created 10 portfolios",
  "iconUrl": "https://example.com/badge-master.png",
  "category": "Achievement",
  "pointsRequired": 100,
  "isActive": true
}
```

#### Update Badge
**PUT** `/api/admin/badges/{badgeId}`

```json
{
  "name": "Updated Badge Name",
  "description": "Updated description",
  "pointsRequired": 150,
  "isActive": true
}
```

#### Delete Badge
**DELETE** `/api/admin/badges/{badgeId}`

**Response:** `200 OK`

### CSV Export

#### Export Users
**GET** `/api/admin/export/users`

**Response:** CSV file download with headers:
```
ID,Username,Email,First Name,Last Name,Role,Enabled,Created At,Last Login
```

#### Export Activities
**GET** `/api/admin/export/activities?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59`

**Response:** CSV file download with headers:
```
ID,User,Action,Type,Description,Entity Type,Entity ID,IP Address,Timestamp
```

#### Export Deployments
**GET** `/api/admin/export/deployments`

**Response:** CSV file download with headers:
```
ID,User,Template,Title,Public URL,Status,Platform,View Count,Created At,Deployed At
```

---

## 📊 Response Formats

### Success Response
```json
{
  "status": "success",
  "data": { ... },
  "message": "Operation completed successfully"
}
```

### Paginated Response
```json
{
  "content": [ ... ],
  "pageable": {
    "sort": { "sorted": false },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 100,
  "totalPages": 5,
  "last": false,
  "first": true,
  "numberOfElements": 20
}
```

---

## ❌ Error Handling

### Error Response Format
```json
{
  "status": "error",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed for field 'email'",
    "details": [
      {
        "field": "email",
        "message": "Email format is invalid"
      }
    ]
  },
  "timestamp": "2025-01-27T16:30:00"
}
```

### Common HTTP Status Codes

| Code | Description |
|------|-------------|
| `200` | Success |
| `201` | Created |
| `400` | Bad Request |
| `401` | Unauthorized |
| `403` | Forbidden |
| `404` | Not Found |
| `409` | Conflict |
| `500` | Internal Server Error |

### Common Error Codes

| Error Code | Description |
|------------|-------------|
| `VALIDATION_ERROR` | Request validation failed |
| `AUTHENTICATION_REQUIRED` | JWT token required |
| `ACCESS_DENIED` | Insufficient permissions |
| `USER_NOT_FOUND` | User does not exist |
| `TEMPLATE_NOT_FOUND` | Template does not exist |
| `DEPLOYMENT_FAILED` | Portfolio deployment failed |
| `DUPLICATE_SUBDOMAIN` | Subdomain already taken |

---

## 🔧 Rate Limiting

Some endpoints have rate limiting applied:

| Endpoint Category | Limit |
|------------------|-------|
| Authentication | 5 requests/minute |
| Portfolio Deployment | 10 requests/hour |
| Resume Generation | 20 requests/hour |
| Admin Operations | 100 requests/minute |

---

**Last Updated:** January 27, 2025  
**API Version:** 2.0.0
