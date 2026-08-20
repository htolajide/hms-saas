import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard'; // Import the new Dashboard
import StaffManagement from './pages/StaffManagement';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';
import PatientManagement from './pages/PatientManagement';
import TriageManagement from './pages/TriageManagement';
import ConsultationPage from './pages/ConsultationPage';
import { LogOut, Building2 } from 'lucide-react';

const DashboardLayout = ({ children }) => {
  const name = localStorage.getItem('fullName');
  const role = localStorage.getItem('role');

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = '/';
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Clean Top Navigation Bar (No more tabs) */}
      <header className="bg-white shadow-sm sticky top-0 z-20">
        <div className="px-6 lg:px-12 py-4">
          <div className="flex justify-between items-center">
            {/* Logo and Home Link */}
            <div 
              className="flex items-center space-x-3 cursor-pointer" 
              onClick={() => window.location.href = '/dashboard'}
            >
              <div className="bg-primary p-2 rounded-lg">
                <Building2 className="h-6 w-6 text-white" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-800 leading-tight">HMS SaaS</h1>
                <p className="text-xs text-gray-500 font-medium">{role}</p>
              </div>
            </div>
            
            {/* User Info and Logout */}
            <div className="flex items-center space-x-4">
              <div className="hidden md:block text-right">
                <p className="text-sm font-semibold text-gray-800">{name}</p>
                <p className="text-xs text-gray-500">Logged in</p>
              </div>
              <button 
                onClick={handleLogout} 
                className="flex items-center space-x-2 px-4 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition text-sm font-medium border border-red-200"
              >
                <LogOut className="h-4 w-4" />
                <span className="hidden sm:inline">Logout</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-grow px-6 lg:px-12 py-8 w-full">
        {children}
      </main>

      {/* Footer */}
      <footer className="bg-white py-4">
        <div className="px-6 lg:px-12 text-center text-xs text-gray-500">
          <p>© 2026 HMS SaaS. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  if (!token) {
    return <Navigate to="/" replace />;
  }
  return <DashboardLayout>{children}</DashboardLayout>;
};

function App() {
  return (
    <Router>
      <Routes>
        {/* PUBLIC ROUTES */}
        <Route path="/" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        
        {/* PROTECTED ROUTES */}
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <Dashboard /> {/* Use the new dynamic Dashboard component */}
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/staff" 
          element={
            <ProtectedRoute>
              <StaffManagement />
            </ProtectedRoute>
          } 
        />
      
        <Route 
          path="/patients" 
          element={
            <ProtectedRoute>
              <PatientManagement />
            </ProtectedRoute>
          } />

        <Route 
          path="/triage" 
          element={
            <ProtectedRoute>
              <TriageManagement />
            </ProtectedRoute>

          } />
          
        <Route 
            path="/consultations" 
            element={
              <ProtectedRoute>
                  <ConsultationPage />
              </ProtectedRoute>
            } />
        
        {/* Placeholder routes for future modules */}
        <Route path="/patients" element={<ProtectedRoute><div className="p-8 text-2xl font-bold text-gray-500">Patient Module Coming Soon...</div></ProtectedRoute>} />
        <Route path="/finance" element={<ProtectedRoute><div className="p-8 text-2xl font-bold text-gray-500">Finance Module Coming Soon...</div></ProtectedRoute>} />
      </Routes>
    </Router>
  );
}

export default App;