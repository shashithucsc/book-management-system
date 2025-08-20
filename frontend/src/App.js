import React, { useState } from 'react';
import './App.css';
import BookManager from './components/BookManager';
import Auth from './components/Auth';

function App() {
  const [user, setUser] = useState(null);

  return (
    <div className="App">
      {user ? (
        <BookManager />
      ) : (
        <Auth onAuth={setUser} />
      )}
    </div>
  );
}

export default App;
