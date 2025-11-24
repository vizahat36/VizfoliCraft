# 🌟 PortfoliCraft — Frontend Documentation

## 📌 Project Purpose

PortfoliCraft allows users to:

1. Register / Login using email & password (JWT-based auth)
2. Enter a LinkedIn or GitHub profile URL
3. Generate portfolio data automatically from backend
4. Edit + customize portfolio
5. Publish & get a unique public URL to share

---

## 🖥 Tech Stack (Frontend)

| Feature       | Tool                            |
| ------------- | ------------------------------- |
| Framework     | React                           |
| Routing       | React Router                    |
| HTTP Requests | Axios                           |
| Global State  | Context API                     |
| Styling       | CSS Modules / Styled Components |
| Icons         | React Icons                     |
| Deployment    | Vercel / Netlify                |

---

## 📂 Folder Structure (Recommended for VS Code)

```
frontend/portfolio-generator/
│── public/
│── src/
│   ├── assets/ (images, icons)
│   ├── components/
│   │   ├── Navbar/
│   │   ├── Loader/
│   │   ├── PortfolioCard/
│   ├── pages/
│   │   ├── Login/
│   │   ├── Register/
│   │   ├── Dashboard/
│   │   ├── PortfolioForm/
│   │   ├── PublishSuccess/
│   │   ├── PublicPortfolio/
│   ├── context/
│   │   ├── AuthContext.jsx
│   ├── services/
│   │   ├── api.js
│   ├── utils/
│   │   ├── validations.js
│   │   ├── authHelpers.js
│   ├── App.jsx
│   ├── index.js
│
├── package.json
├── README.md
```

---

## 🌐 FRONTEND FLOW (END-TO-END)

```
User visits site
     ↓
Login / Register (JWT stored in localStorage)
     ↓
Dashboard
     ↓
Inputs LinkedIn / GitHub URL
     ↓
Sends request to backend /api/portfolio/create
     ↓
Receives imported profile data
     ↓
User edits + selects theme
     ↓
Click Publish
     ↓
Backend returns public portfolio link (/u/username)
     ↓
User can share & view the portfolio public page
```

---

## 🔑 PAGE-WISE FUNCTIONAL BREAKDOWN

### 🔹 1. Login Page

* Email + Password input
* Validation + Error Toast
* On success → store JWT in localStorage & redirect to Dashboard

### 🔹 2. Register Page

* Email + Username + Password
* Strong password indicator
* On success → login automatically

### 🔹 3. Dashboard

* Greeting (username)
* Button: "Create New Portfolio"
* Button: "View My Published Portfolio"
* Logout

### 🔹 4. Portfolio Form Page

* Input: GitHub / LinkedIn URL
* Submit → API call → returns imported profile data
* Shows preview of:
  * Name, Bio
  * Skills
  * Projects
  * Social links
* User can edit fields
* Template selection (e.g., Modern / Minimal / Dark)
* Button: Publish

### 🔹 5. Publish Success Page

* Shows message: "🎉 Portfolio Published Successfully"
* Public URL: `https://portfolicraft.me/u/<username>`
* Buttons:
  * Open portfolio
  * Copy link
  * Share

### 🔹 6. Public Portfolio Page

* Clean, responsive portfolio page
* Contains:
  * Name
  * Avatar
  * Bio
  * Projects
  * Skills
  * Social links
* Users who don't own profile → Read-only

---

## 🧠 REACT CONTEXT — Global Auth Workflow

```javascript
AuthContext:
  user → { id, username, email }
  token → JWT stored in localStorage
  login() → call /auth/login → set user + token
  register() → call /auth/signup
  logout() → clear token + user
```

---

## 🛰 API CONNECTION POINTS (Frontend → Spring Boot Backend)

| Action                    | Method | Endpoint                    | Headers                  |
| ------------------------- | ------ | --------------------------- | ------------------------ |
| Login                     | POST   | `/api/auth/login`           | -                        |
| Register                  | POST   | `/api/auth/register`        | -                        |
| Create/Import Portfolio   | POST   | `/api/portfolio/create`     | Authorization: Bearer {} |
| Get Logged User Portfolio | GET    | `/api/portfolio/me`         | Authorization: Bearer {} |
| Update Portfolio          | PUT    | `/api/portfolio/update`     | Authorization: Bearer {} |
| Publish Portfolio         | POST   | `/api/portfolio/publish`    | Authorization: Bearer {} |
| Fetch public portfolio    | GET    | `/u/:username`              | -                        |

Frontend must always send token for protected routes:

```javascript
Authorization: Bearer <JWT>
```

Stored in localStorage:

```javascript
localStorage.setItem("token", jwt);
```

---

## 📝 API REQUEST/RESPONSE EXAMPLES

### 1. Login
```javascript
// Request
POST /api/auth/login
{
  "username": "john_doe",
  "password": "password123"
}

// Response
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": "64f8a1234567890abcdef123",
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

### 2. Register
```javascript
// Request
POST /api/auth/register
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}

// Response
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": "64f8a1234567890abcdef123",
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

### 3. Create Portfolio from GitHub/LinkedIn URL
```javascript
// Request
POST /api/portfolio/create
Headers: Authorization: Bearer <token>
{
  "profileUrl": "https://github.com/johndoe",
  "platform": "GITHUB"  // or "LINKEDIN"
}

// Response
{
  "id": "64f8a1234567890abcdef456",
  "username": "john_doe",
  "name": "John Doe",
  "bio": "Full Stack Developer passionate about open source",
  "location": "San Francisco, CA",
  "avatarUrl": "https://avatars.githubusercontent.com/u/123456",
  "website": "https://johndoe.dev",
  "githubUrl": "https://github.com/johndoe",
  "linkedinUrl": null,
  "skills": ["JavaScript", "React", "Node.js", "Python"],
  "projects": [
    {
      "name": "awesome-project",
      "description": "An awesome open source project",
      "url": "https://github.com/johndoe/awesome-project",
      "language": "JavaScript",
      "stars": 125
    }
  ],
  "isPublished": false,
  "template": "MODERN"
}
```

### 4. Update Portfolio
```javascript
// Request
PUT /api/portfolio/update
Headers: Authorization: Bearer <token>
{
  "bio": "Updated bio text",
  "skills": ["JavaScript", "React", "Node.js", "Python", "Docker"],
  "template": "MINIMAL",
  "customCSS": ".header { background: #0066cc; }"
}

// Response
{
  "id": "64f8a1234567890abcdef456",
  "message": "Portfolio updated successfully"
}
```

### 5. Publish Portfolio
```javascript
// Request
POST /api/portfolio/publish
Headers: Authorization: Bearer <token>
{
  "isPublic": true
}

// Response
{
  "success": true,
  "publicUrl": "https://portfolicraft.me/u/john_doe",
  "message": "Portfolio published successfully"
}
```

### 6. Get User's Portfolio
```javascript
// Request
GET /api/portfolio/me
Headers: Authorization: Bearer <token>

// Response
{
  "id": "64f8a1234567890abcdef456",
  "username": "john_doe",
  "name": "John Doe",
  "bio": "Full Stack Developer",
  "isPublished": true,
  "publicUrl": "https://portfolicraft.me/u/john_doe",
  // ... rest of portfolio data
}
```

### 7. View Public Portfolio
```javascript
// Request
GET /u/john_doe

// Response (HTML page or JSON based on Accept header)
{
  "username": "john_doe",
  "name": "John Doe",
  "bio": "Full Stack Developer",
  "avatarUrl": "https://avatars.githubusercontent.com/u/123456",
  "skills": ["JavaScript", "React", "Node.js"],
  "projects": [...],
  "template": "MODERN"
}
```

---

## 📌 TASK CHECKLIST (Paste directly in VS Code GitHub project)

```markdown
📍 FRONTEND DEVELOPMENT CHECKLIST — PortfoliCraft

## Setup & Configuration
- [ ] Initialize React + dependencies
- [ ] Create file structure as per docs
- [ ] Install Axios + React Router + React Icons
- [ ] Configure environment variables (.env)
- [ ] Set up proxy for API calls

## Authentication
- [ ] Build AuthContext (login, register, logout, protect routes)
- [ ] Create Login page
- [ ] Create Register page
- [ ] Implement form validation
- [ ] Save JWT in localStorage
- [ ] Redirect to Dashboard on success
- [ ] Handle authentication errors
- [ ] Add logout functionality

## Dashboard
- [ ] Create Dashboard page layout
- [ ] Display user greeting
- [ ] Add "Create New Portfolio" button
- [ ] Add "View Published Portfolio" button
- [ ] Add logout button
- [ ] Handle navigation

## Portfolio Creation
- [ ] Build PortfolioForm page
- [ ] Add URL input field (GitHub/LinkedIn)
- [ ] Platform selection dropdown
- [ ] Connect POST /api/portfolio/create
- [ ] Display loading state during fetch
- [ ] Handle API errors
- [ ] Display imported profile data in preview
- [ ] Make all fields editable
- [ ] Add skills management (add/remove)
- [ ] Add projects list editing
- [ ] Implement theme selection dropdown
- [ ] Save draft functionality (optional)

## Portfolio Publishing
- [ ] Add "Publish" button
- [ ] Connect POST /api/portfolio/publish
- [ ] Display PublishSuccess page
- [ ] Show public URL
- [ ] Add copy link button
- [ ] Add share buttons (social media)
- [ ] Add "Open Portfolio" button

## Public Portfolio View
- [ ] Create PublicPortfolio page (/u/:username route)
- [ ] Fetch portfolio data from GET /u/:username
- [ ] Render portfolio based on selected template
- [ ] Implement MODERN template
- [ ] Implement MINIMAL template
- [ ] Implement DARK template
- [ ] Make fully responsive
- [ ] Add meta tags for SEO

## Reusable Components
- [ ] Create Navbar component
- [ ] Create Footer component
- [ ] Create LoadingSpinner component
- [ ] Create ErrorMessage component
- [ ] Create Button component
- [ ] Create Input component
- [ ] Create Card component

## Services & API
- [ ] Create api.js with axios instance
- [ ] Add request interceptor (attach JWT)
- [ ] Add response interceptor (handle errors)
- [ ] Create authService.js
- [ ] Create portfolioService.js
- [ ] Implement error handling

## Styling & UX
- [ ] Create global styles
- [ ] Implement responsive design
- [ ] Add loading states everywhere
- [ ] Add error messages
- [ ] Add success notifications
- [ ] Add form validation feedback
- [ ] Test on mobile devices
- [ ] Test on different browsers
- [ ] Add animations/transitions

## Testing & Deployment
- [ ] Test authentication flow
- [ ] Test portfolio creation flow
- [ ] Test portfolio editing
- [ ] Test publishing flow
- [ ] Test public portfolio view
- [ ] Fix CORS issues (if any)
- [ ] Build production bundle
- [ ] Deploy to Vercel/Netlify
- [ ] Configure custom domain (optional)
```

---

## 🚀 Getting Started - Quick Setup

### 1. Install Dependencies
```bash
cd frontend/portfolio-generator
npm install axios react-router-dom react-icons
```

### 2. Create .env File
```env
REACT_APP_API_URL=http://localhost:8080
```

### 3. Update package.json (add proxy)
```json
{
  "proxy": "http://localhost:8080"
}
```

### 4. Create API Service (src/services/api.js)
```javascript
import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor - attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 5. Create Auth Context (src/context/AuthContext.jsx)
```javascript
import React, { createContext, useState, useEffect } from 'react';
import api from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      // Verify token on mount
      localStorage.setItem('token', token);
    }
    setLoading(false);
  }, [token]);

  const login = async (username, password) => {
    const response = await api.post('/api/auth/login', { username, password });
    const { token: newToken, user: userData } = response.data;
    
    setToken(newToken);
    setUser(userData);
    localStorage.setItem('token', newToken);
    
    return response.data;
  };

  const register = async (username, email, password, firstName, lastName) => {
    const response = await api.post('/api/auth/register', {
      username, email, password, firstName, lastName
    });
    const { token: newToken, user: userData } = response.data;
    
    setToken(newToken);
    setUser(userData);
    localStorage.setItem('token', newToken);
    
    return response.data;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
```

---

## 💬 You are now ready to begin coding

After frontend UI pages are built, the system will:

* Connect backend APIs automatically (using axios)
* Handle JWT authentication
* Fetch and display portfolio data
* Allow editing and publishing

### Development Workflow

1. Start backend: `cd backend/viztoufolicraft && mvn spring-boot:run`
2. Start frontend: `cd frontend/portfolio-generator && npm start`
3. Open browser: `http://localhost:3000`

### When the checklist is 100% complete → frontend is production ready

---

## 🐛 Common Issues & Solutions

### CORS Errors
If you get CORS errors, the backend already has `@CrossOrigin(origins = "*")` on controllers. If issues persist, check that both frontend and backend are running.

### Authentication Issues
- Ensure JWT token is being saved to localStorage
- Check that Authorization header is being sent with requests
- Verify token format: `Bearer <token>`

### API Connection Issues
- Verify backend is running on port 8080
- Check .env file has correct API_URL
- Check network tab in browser DevTools for failed requests

---

**Last Updated**: November 24, 2025  
**Version**: 1.0  
**Status**: Ready for Development
