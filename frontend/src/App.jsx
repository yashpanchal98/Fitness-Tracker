import React, { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from 'react-oidc-context';
import { Activity } from 'lucide-react';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import { setAuthToken } from './services/api';

function App() {
  const auth = useAuth();

  useEffect(() => {
    if (auth.isAuthenticated) {
      setAuthToken(auth.user?.access_token);
    } else {
      setAuthToken(null);
    }
  }, [auth.isAuthenticated, auth.user]);

  if (auth.isLoading) {
    return <div className="container" style={{ textAlign: 'center', marginTop: '100px' }}>Loading authentication...</div>;
  }

  return (
    <BrowserRouter>
      <nav className="navbar">
        <a href="/" className="navbar-brand">
          <Activity color="#6366f1" size={32} />
          <span>FitTracker Sync</span>
        </a>
        <div>
          {auth.isAuthenticated ? (
            <button className="btn-secondary" onClick={() => void auth.signoutRedirect()}>
              Sign out
            </button>
          ) : (
            <button className="btn-primary" onClick={() => void auth.signinRedirect()}>
              Sign In / Register
            </button>
          )}
        </div>
      </nav>

      <Routes>
        <Route path="/" element={auth.isAuthenticated ? <Navigate to="/dashboard" /> : <Home />} />
        <Route path="/dashboard" element={auth.isAuthenticated ? <Dashboard /> : <Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
