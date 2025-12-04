import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';

// --- STYLED LOGIN COMPONENT ---
const Login = () => {
  const handleLogin = (role) => {
    let tokenPayload = "";
    // Creating fake tokens for testing
    if (role === 'Admin') tokenPayload = "eyJyb2xlIjoiQWRtaW4ifQ";
    if (role === 'Citizen') tokenPayload = "eyJyb2xlIjoiQ2l0aXplbiJ9";
    
    const fakeToken = `header.${tokenPayload}.signature`;
    
    localStorage.setItem("authToken", fakeToken);
    window.location.href = "/"; // Refresh to apply login
  };

  return (
    // Outer Container (Gray Background + Centered)
    <div style={{
      height: '100vh',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      backgroundColor: '#f0f2f5'
    }}>
      
      {/* Inner Card (White Box) */}
      <div style={{
        backgroundColor: 'white',
        padding: '40px',
        borderRadius: '10px',
        boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
        textAlign: 'center',
        width: '300px'
      }}>
        <h2 style={{ marginBottom: '20px', color: '#333' }}>Login Page</h2>
        <p style={{ marginBottom: '20px', color: '#666' }}>Select your role:</p>
        
        {/* Citizen Button */}
        <button 
          onClick={() => handleLogin('Citizen')} 
          style={{ 
            display: 'block', 
            width: '100%', 
            padding: '10px', 
            marginBottom: '10px',
            backgroundColor: '#28a745', 
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer'
          }}
        >
          Login as Citizen
        </button>

        {/* Admin Button */}
        <button 
          onClick={() => handleLogin('Admin')} 
          style={{ 
            display: 'block', 
            width: '100%', 
            padding: '10px',
            backgroundColor: '#dc3545', 
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer'
          }}
        >
          Login as Admin
        </button>
      </div>
    </div>
  );
};

// --- DASHBOARD PAGES ---
const AdminPanel = () => {
  return (
    <div style={{ padding: 20, textAlign: 'center' }}>
      <h1 style={{ color: 'red' }}>Admin Verification Panel</h1>
      <p>Only Admins can see this.</p>
    </div>
  );
};

const CitizenDash = () => {
  return (
    <div style={{ padding: 20, textAlign: 'center' }}>
      <h1 style={{ color: 'green' }}>Citizen Case Submission</h1>
      <p>Submit your legal request here.</p>
    </div>
  );
};

const Home = () => {
  return (
    <div style={{ padding: 50, textAlign: 'center' }}>
      <h1>Welcome to Legal Aid Platform</h1>
      <p>Connecting citizens with justice.</p>
    </div>
  );
};

// --- MAIN APP ---
function App() {
  return (
    <BrowserRouter>
      {/* Navigation Bar */}
      <nav style={{ padding: '15px 30px', background: '#333', color: 'white', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Link to="/" style={{ color: 'white', textDecoration: 'none', marginRight: 20, fontWeight: 'bold' }}>Home</Link>
          <Link to="/login" style={{ color: 'white', textDecoration: 'none', marginRight: 20 }}>Login</Link>
          <Link to="/citizen" style={{ color: '#90ee90', textDecoration: 'none', marginRight: 20 }}>Citizen Dash</Link>
          <Link to="/admin" style={{ color: '#ffcccb', textDecoration: 'none' }}>Admin Panel</Link>
        </div>
        
        <button 
          onClick={() => { localStorage.clear(); window.location.reload(); }} 
          style={{ padding: '5px 15px', cursor: 'pointer', backgroundColor: 'transparent', border: '1px solid white', color: 'white', borderRadius: '4px' }}
        >
          Logout
        </button>
      </nav>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        
        <Route element={<ProtectedRoute allowedRoles={['Citizen']} />}>
          <Route path="/citizen" element={<CitizenDash />} />
        </Route>

        <Route element={<ProtectedRoute allowedRoles={['Admin']} />}>
          <Route path="/admin" element={<AdminPanel />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;