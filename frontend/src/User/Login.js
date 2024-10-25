import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // 페이지 이동을 위한 useNavigate 훅
import './Login.css';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate(); // 페이지 이동을 위한 네비게이트 함수

    const handleLogin = async (event) => {
        event.preventDefault();
        try {
            const response = await fetch('http://localhost:8080/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                // 토큰이 헤더에 담겨서 응답될 것이므로 헤더에서 가져옴
                const token = response.headers.get('Authorization'); // 'Authorization' 헤더에서 토큰 가져오기

                if (token) {
                    console.log('Login Success, Token:', token);
                    localStorage.setItem('token', token); // 로컬 스토리지에 토큰 저장
                    navigate('/game/create'); // 게임 생성 페이지로 이동
                } else {
                    console.error('Token not found in the response!');
                    alert('Token not found in the response!');
                }
            } else {
                const data = await response.json();
                console.error('Login Error:', data);
                alert('Login failed!');
            }
        } catch (error) {
            console.error('Login request failed:', error);
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <label>
                    Username:
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </label>
                <label>
                    Password:
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </label>
                <button type="submit">Login</button>
            </form>
        </div>
    );
}

export default Login;