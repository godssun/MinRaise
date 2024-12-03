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
                body: JSON.stringify({ username, password }),
            });

            if (response.ok) {
                // 'Authorization' 헤더에서 토큰 가져오기
                const bearerToken = response.headers.get('Authorization');
                if (bearerToken) {
                    // 'Bearer ' 접두사 제거 후 순수 토큰 값만 저장
                    const token = bearerToken.replace('Bearer ', '');
                    console.log('Login Success, Token:', token);

                    // 토큰을 로컬 스토리지에 저장
                    localStorage.setItem('token', token);

                    // 게임 생성 페이지로 이동
                    navigate('/game/create');
                } else {
                    console.error('Token not found in the response!');
                    alert('로그인에 실패했습니다: 토큰이 반환되지 않았습니다.');
                }
            } else {
                const data = await response.json();
                console.error('Login Error:', data);
                alert('로그인 실패: ' + (data.message || '알 수 없는 오류'));
            }
        } catch (error) {
            console.error('Login request failed:', error);
            alert('로그인 요청이 실패했습니다. 다시 시도해주세요.');
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