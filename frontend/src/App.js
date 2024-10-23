import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import SignUp from './User/SignUp';
import Login from './User/Login';
import './Welcome.css'; // Welcome 스타일 추가

function Welcome() {
    return (
        <div className="welcome-container">
            <h1 className="welcome-title">Welcome to Our Application!</h1>
            <div className="welcome-buttons">
                <Link to="/login"><button>Login</button></Link>
                <Link to="/signup"><button>Sign Up</button></Link>
            </div>
        </div>
    );
}

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Welcome />} />
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<SignUp />} />
            </Routes>
        </Router>
    );
}

export default App;