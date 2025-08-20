
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './BookManager.css';

const API_BASE_URL = 'http://localhost:8081/api';

export default function BookManager() {
  const [books, setBooks] = useState([]);
  const [form, setForm] = useState({ 
    title: '', 
    author: '', 
    description: '', 
    isbn: '', 
    year: '' 
  });
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${API_BASE_URL}/books`);
      setBooks(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch books');
      console.error('Error fetching books:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const payload = {
        ...form,
        year: form.year ? parseInt(form.year) : null
      };

      if (editingId) {
        await axios.put(`${API_BASE_URL}/books/${editingId}`, payload);
      } else {
        await axios.post(`${API_BASE_URL}/books`, payload);
      }
      
      setForm({ title: '', author: '', description: '', isbn: '', year: '' });
      setEditingId(null);
      await fetchBooks();
    } catch (err) {
      setError('Failed to save book');
      console.error('Error saving book:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (book) => {
    setForm({
      title: book.title || '',
      author: book.author || '',
      description: book.description || '',
      isbn: book.isbn || '',
      year: book.year ? book.year.toString() : ''
    });
    setEditingId(book.id);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this book?')) {
      return;
    }
    
    setLoading(true);
    setError('');
    try {
      await axios.delete(`${API_BASE_URL}/books/${id}`);
      await fetchBooks();
    } catch (err) {
      setError('Failed to delete book');
      console.error('Error deleting book:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setForm({ title: '', author: '', description: '', isbn: '', year: '' });
    setEditingId(null);
    setError('');
  };

  return (
    <div className="book-manager-container">
      <div className="book-manager-card">
        <h1>📚 Book Management System</h1>
        
        <form className="book-form" onSubmit={handleSubmit}>
          {error && <div className="error-message">{error}</div>}
          
          <div className="form-row">
            <input 
              name="title" 
              value={form.title} 
              onChange={handleChange} 
              placeholder="Book Title" 
              required 
              disabled={loading} 
            />
            <input 
              name="author" 
              value={form.author} 
              onChange={handleChange} 
              placeholder="Author" 
              required 
              disabled={loading} 
            />
          </div>
          
          <div className="form-row">
            <input 
              name="isbn" 
              value={form.isbn} 
              onChange={handleChange} 
              placeholder="ISBN" 
              disabled={loading} 
            />
            <input 
              name="year" 
              type="number" 
              value={form.year} 
              onChange={handleChange} 
              placeholder="Publication Year" 
              min="1900" 
              max="2024"
              disabled={loading} 
            />
          </div>
          
          <textarea 
            name="description" 
            value={form.description} 
            onChange={handleChange} 
            placeholder="Book Description" 
            disabled={loading} 
          />
          
          <div className="form-actions">
            <button type="submit" className="book-btn primary" disabled={loading}>
              {editingId ? 'Update Book' : 'Add Book'}
            </button>
            {editingId && (
              <button type="button" className="book-btn cancel" onClick={handleCancel} disabled={loading}>
                Cancel
              </button>
            )}
          </div>
        </form>

        <div className="book-list">
          <h2>📖 Book Collection</h2>
          {loading ? (
            <div className="loading">Loading books...</div>
          ) : books.length === 0 ? (
            <div className="no-books">No books found. Add your first book above!</div>
          ) : (
            <div className="books-grid">
              {books.map(book => (
                <div key={book.id} className="book-card">
                  <h3>{book.title}</h3>
                  <p><strong>Author:</strong> {book.author}</p>
                  {book.isbn && <p><strong>ISBN:</strong> {book.isbn}</p>}
                  {book.year && <p><strong>Year:</strong> {book.year}</p>}
                  {book.description && <p><strong>Description:</strong> {book.description}</p>}
                  <div className="book-actions">
                    <button 
                      className="book-btn edit" 
                      onClick={() => handleEdit(book)} 
                      disabled={loading}
                    >
                      Edit
                    </button>
                    <button 
                      className="book-btn delete" 
                      onClick={() => handleDelete(book.id)} 
                      disabled={loading}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
