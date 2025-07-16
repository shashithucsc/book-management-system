import React, { useState } from 'react';
import axios from 'axios';
import './Login.css';

export default function Login({ onLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isRegister, setIsRegister] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = async () => {
    if (!username.trim() || !password.trim()) {
      setError('Please fill in all fields');
      return;
    }

    setLoading(true);
    setError('');
    
    try {
      const res = await axios.post('http://localhost:8081/auth/login', { 
        username: username.trim(), 
        password: password.trim() 
      }, {
        timeout: 5000 // 5 seconds timeout
      });
      
      if (res.status === 200) {
        alert('Login successful!');
        onLogin(true);
      }
    } catch (e) {
      if (e.response) {
        setError(e.response.data || 'Login failed');
      } else {
        setError('Network error. Please check if the server is running.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async () => {
    if (!username.trim() || !password.trim()) {
      setError('Please fill in all fields');
      return;
    }

    if (password.length < 4) {
      setError('Password must be at least 4 characters long');
      return;
    }

    setLoading(true);
    setError('');
    
    try {
      const res = await axios.post('http://localhost:8081/auth/register', { 
        username: username.trim(), 
        password: password.trim() 
      });
      
      if (res.status === 200) {
        alert('Registration successful! You can now login.');
        setIsRegister(false);
        setUsername('');
        setPassword('');
      }
    } catch (e) {
      if (e.response) {
        setError(e.response.data || 'Registration failed');
      } else {
        setError('Network error. Please check if the server is running.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (isRegister) {
      handleRegister();
    } else {
      handleLogin();
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h2>{isRegister ? 'Create Account' : 'Welcome Back'}</h2>
          <p>{isRegister ? 'Sign up to get started' : 'Sign in to your account'}</p>
        </div>
        
        <form onSubmit={handleSubmit} className="login-form">
          {error && <div className="error-message">{error}</div>}
          
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input 
              id="username"
              type="text"
              placeholder="Enter your username" 
              value={username}
              onChange={e => setUsername(e.target.value)}
              disabled={loading}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input 
              id="password"
              type="password" 
              placeholder="Enter your password" 
              value={password}
              onChange={e => setPassword(e.target.value)}
              disabled={loading}
              required
            />
          </div>
          
          <button 
            type="submit" 
            className={`login-button ${loading ? 'loading' : ''}`}
            disabled={loading}
          >
            {loading ? 'Please wait...' : (isRegister ? 'Sign Up' : 'Sign In')}
          </button>
        </form>
        
        <div className="login-footer">
          <p>
            {isRegister ? 'Already have an account?' : "Don't have an account?"}{' '}
            <button 
              type="button"
              className="switch-button"
              onClick={() => {
                setIsRegister(!isRegister);
                setError('');
                setUsername('');
                setPassword('');
              }}
              disabled={loading}
            >
              {isRegister ? 'Sign In' : 'Sign Up'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}
