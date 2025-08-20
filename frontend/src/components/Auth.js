import React, { useState } from 'react';
import axios from 'axios';
import './Auth.css';

const API_BASE_URL = 'http://localhost:8081/api';

export default function Auth({ onAuth }) {
  const [mode, setMode] = useState('login'); // 'login' | 'register'
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [form, setForm] = useState({
    username: '',
    password: '',
    email: '',
    fullName: ''
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const toggleMode = () => {
    setError('');
    setMode(mode === 'login' ? 'register' : 'login');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (mode === 'login') {
        const res = await axios.post(`${API_BASE_URL}/users/login`, {
          username: form.username,
          password: form.password
        });
        onAuth(res.data);
      } else {
        await axios.post(`${API_BASE_URL}/users/register`, {
          username: form.username,
          password: form.password,
          email: form.email,
          fullName: form.fullName
        });
        // Auto switch to login after successful registration
        setMode('login');
      }
    } catch (err) {
      setError(
        err?.response?.data || (mode === 'login' ? 'Login failed' : 'Registration failed')
      );
      // eslint-disable-next-line no-console
      console.error('Auth error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="glass-card auth-card">
        <div className="auth-header">
          <h1>BookApp</h1>
          <p className="subtitle">{mode === 'login' ? 'Welcome back' : 'Create your account'}</p>
        </div>

        <div className="auth-toggle">
          <button
            className={`toggle-btn ${mode === 'login' ? 'active' : ''}`}
            onClick={() => setMode('login')}
            disabled={loading}
          >
            Login
          </button>
          <button
            className={`toggle-btn ${mode === 'register' ? 'active' : ''}`}
            onClick={() => setMode('register')}
            disabled={loading}
          >
            Register
          </button>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          {error && <div className="auth-error">{String(error)}</div>}

          <div className="form-row single">
            <input
              name="username"
              value={form.username}
              onChange={handleChange}
              placeholder="Username"
              autoComplete="username"
              required
              disabled={loading}
            />
          </div>

          {mode === 'register' && (
            <>
              <div className="form-row single">
                <input
                  name="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="Email"
                  type="email"
                  autoComplete="email"
                  required
                  disabled={loading}
                />
              </div>
              <div className="form-row single">
                <input
                  name="fullName"
                  value={form.fullName}
                  onChange={handleChange}
                  placeholder="Full name (optional)"
                  disabled={loading}
                />
              </div>
            </>
          )}

          <div className="form-row single">
            <input
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Password"
              type="password"
              autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
              required
              disabled={loading}
            />
          </div>

          <div className="form-actions">
            <button className="primary-btn" type="submit" disabled={loading}>
              {loading ? 'Please wait…' : mode === 'login' ? 'Login' : 'Create account'}
            </button>
            <button className="link-btn" type="button" onClick={toggleMode} disabled={loading}>
              {mode === 'login' ? "Don't have an account? Register" : 'Already have an account? Login'}
            </button>
          </div>
        </form>
      </div>

      <div className="auth-footer">© {new Date().getFullYear()} BookApp • Dark Glass Theme</div>
    </div>
  );
} 