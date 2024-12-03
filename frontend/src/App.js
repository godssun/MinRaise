import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SignUp from './User/SignUp';
import Login from './User/Login';
import './Welcome.css';
import GameCreationPage from './Game/GameCreationPage';
import PlayerAddForm from './Game/PlayerAddForm';
import BettingForm from './Game/BettingForm';


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
                <Route path="/game/create" element={<GameCreationPage />} /> {/* 게임 생성 페이지 추가 */}
                <Route path="/game/:gameId/add-players" element={<PlayerAddForm />} />
                <Route path="/game/:gameId/betting" element={<BettingForm />} /> {/* BettingForm 경로 추가 */}

            </Routes>
        </Router>
    );
}

export default App;