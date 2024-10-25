import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignUp from './User/SignUp';
import Login from './User/Login';
import GameCreate from './Game/GameCreate';
import PlayerAdd from './Game/PlayerAdd';
import './Welcome.css';

function Welcome() {
    return (
        <div className="welcome-container">
            <h1 className="welcome-title">Welcome to Our Application!</h1>
            <div className="welcome-buttons">
                <a href="/login"><button>Login</button></a>
                <a href="/signup"><button>Sign Up</button></a>
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
                <Route path="/game/create" element={<GameCreate />} />
                <Route path="/players/add/:gameId" element={<PlayerAdd />} />
            </Routes>
        </Router>
    );
}

export default App;