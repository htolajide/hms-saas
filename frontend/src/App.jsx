import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';

// Placeholder for Dashboard (we will build this next!)
const Dashboard = () => {
  const role = localStorage.getItem('role');
  const name = localStorage.getItem('fullName');
  
  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="bg-white p-6 rounded-xl shadow-md">
        <h1 className="text-2xl font-bold text-gray-800">Welcome, {name}!</h1>
        <p className="text-gray-600 mt-2">Your role: <span className="font-semibold text-primary">{role}</span></p>
        <button 
          onClick={() => {
            localStorage.clear();
            window.location.href = '/';
          }}
          className="mt-4 px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

// Protected Route Wrapper
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  if (!token) {
    return <Navigate to="/" replace />;
  }
  return children;
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } 
        />
      </Routes>
    </Router>
  );
}

export default App;