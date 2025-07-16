
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './BookManager.css';

export default function BookManager() {
  const [books, setBooks] = useState([]);
  const [form, setForm] = useState({ title: '', author: '', description: '', isbn: '', published_date: '' });
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const res = await axios.get('http://localhost:8081/books');
      setBooks(res.data);
    } catch (e) {
      setError('Failed to fetch books');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      // Map published_date to publishedDate for backend compatibility
      const payload = { ...form, publishedDate: form.published_date };
      delete payload.published_date;
      if (editingId) {
        await axios.put(`http://localhost:8081/books/${editingId}`, payload);
      } else {
        await axios.post('http://localhost:8081/books', payload);
      }
      setForm({ title: '', author: '', description: '', isbn: '', published_date: '' });
      setEditingId(null);
      fetchBooks();
    } catch (e) {
      setError('Failed to save book');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = book => {
    setForm({
      title: book.title || '',
      author: book.author || '',
      description: book.description || '',
      isbn: book.isbn || '',
      published_date: book.publishedDate ? book.publishedDate.substring(0, 10) : ''
    });
    setEditingId(book.id);
  };

  const handleDelete = async id => {
    setLoading(true);
    setError('');
    try {
      await axios.delete(`http://localhost:8081/books/${id}`);
      fetchBooks();
    } catch (e) {
      setError('Failed to delete book');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setForm({ title: '', author: '', description: '', isbn: '', published_date: '' });
    setEditingId(null);
    setError('');
  };

  return (
    <div className="book-manager-container">
      <div className="book-manager-card">
        <h2>Book List</h2>
        <form className="book-form" onSubmit={handleSubmit}>
          {error && <div className="error-message">{error}</div>}
          <div className="form-row">
            <input name="title" value={form.title} onChange={handleChange} placeholder="Title" required disabled={loading} />
            <input name="author" value={form.author} onChange={handleChange} placeholder="Author" required disabled={loading} />
          </div>
          <div className="form-row">
            <input name="isbn" value={form.isbn} onChange={handleChange} placeholder="ISBN" disabled={loading} />
            <input name="published_date" type="date" value={form.published_date} onChange={handleChange} placeholder="Published Date" disabled={loading} />
          </div>
          <textarea name="description" value={form.description} onChange={handleChange} placeholder="Description" disabled={loading} />
          <div className="form-actions">
            <button type="submit" className="book-btn" disabled={loading}>
              {editingId ? 'Update Book' : 'Add Book'}
            </button>
            {editingId && <button type="button" className="book-btn cancel" onClick={handleCancel} disabled={loading}>Cancel</button>}
          </div>
        </form>
        <div className="book-list">
          {loading ? <div>Loading...</div> : (
            books.length === 0 ? <div>No books found.</div> : (
              <table className="book-table">
                <thead>
                  <tr>
                    <th>Title</th>
                    <th>Author</th>
                    <th>ISBN</th>
                    <th>Published</th>
                    <th>Description</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {books.map(book => (
                    <tr key={book.id}>
                      <td>{book.title}</td>
                      <td>{book.author}</td>
                      <td>{book.isbn}</td>
                      <td>{book.publishedDate ? book.publishedDate.substring(0, 10) : ''}</td>
                      <td>{book.description}</td>
                      <td>
                        <button className="book-btn edit" onClick={() => handleEdit(book)} disabled={loading}>Edit</button>
                        <button className="book-btn delete" onClick={() => handleDelete(book.id)} disabled={loading}>Delete</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )
          )}
        </div>
      </div>
    </div>
  );
}
