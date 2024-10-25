import React, { useState } from 'react';
import './SignUp.css';  // 회원가입 페이지 전용 스타일
import { useNavigate } from 'react-router-dom';  // 리디렉션을 위한 useNavigate 추가

function SignUp() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const navigate = useNavigate();  // useNavigate 훅 사용

    const handleSignUp = async (event) => {
        event.preventDefault();
        const response = await fetch('http://localhost:8080/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password, email })
        });
        const data = await response.json();
        if (response.ok) {
            console.log('SignUp Success:', data);
            alert('Registration successful!');
            navigate('/login');  // 회원가입 성공 시 로그인 페이지로 리디렉션
        } else {
            console.log('SignUp Error:', data);
            alert('Registration failed!');
        }
    };

    return (
        <div className="sign-up-container">
            <h2>Sign Up</h2>
            <form onSubmit={handleSignUp}>
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
                <label>
                    Email:
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </label>
                <button type="submit">Sign Up</button>
            </form>
        </div>
    );
}

export default SignUp;