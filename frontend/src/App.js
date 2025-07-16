import React, { useState } from 'react';
import './App.css';
import Login from './components/Login';
import BookManager from './components/BookManager';

function App() {
  const [loggedIn, setLoggedIn] = useState(false);
  return (
    <div className="App">
      {loggedIn ? <BookManager /> : <Login onLogin={setLoggedIn} />}
    </div>
  );
}

export default App;
