import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './contexts/AuthContext';
import { theme } from './theme/theme';
import ProtectedRoute from './components/ProtectedRoute';

// Public Site Pages
import Landing from './publicSite/pages/Landing';
import Login from './publicSite/pages/Login';
import Signup from './publicSite/pages/Signup';
import Dashboard from './publicSite/pages/Dashboard';
import Templates from './publicSite/pages/Templates';
import TemplateCustomizer from './publicSite/pages/TemplateCustomizer';

// Admin Panel Pages
import AdminDashboard from './adminPanel/pages/AdminDashboard';
import UserManagement from './adminPanel/pages/UserManagement';

// Portfolio Viewer
import PortfolioViewer from './publicSite/pages/PortfolioViewer';

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<Landing />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/templates" element={<Templates />} />
            
            {/* Protected User Routes */}
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } />
            <Route path="/customize/:templateId" element={
              <ProtectedRoute>
                <TemplateCustomizer />
              </ProtectedRoute>
            } />
            
            {/* Admin Routes */}
            <Route path="/admin" element={
              <ProtectedRoute adminOnly>
                <AdminDashboard />
              </ProtectedRoute>
            } />
            <Route path="/admin/users" element={
              <ProtectedRoute adminOnly>
                <UserManagement />
              </ProtectedRoute>
            } />
            
            {/* Portfolio Viewer */}
            <Route path="/portfolio/:username/:portfolioId" element={<PortfolioViewer />} />
            
            {/* Catch all route */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
