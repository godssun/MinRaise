import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './PlayerAddForm.css';

function PlayerAddForm() {
    const { gameId } = useParams(); // URL에서 gameId 추출
    const navigate = useNavigate();
    const [playerNames, setPlayerNames] = useState([]);
    const [positions, setPositions] = useState([]); // 포지션 배열
    const [loading, setLoading] = useState(true);

    // 포지션 생성 함수
    const generatePositions = (maxPlayers) => {
        const defaultPositions = ['SB', 'BB', 'UTG', 'UTG+1', 'UTG+2', 'MP', 'MP+1', 'CO', 'BTN'];
        return defaultPositions.slice(0, maxPlayers);
    };

    useEffect(() => {
        const fetchGameInfo = async () => {
            const token = localStorage.getItem('token');
            if (!token) {
                alert('You must log in to access this page.');
                navigate('/login');
                return;
            }

            try {
                const response = await fetch(`http://localhost:8080/api/games/${gameId}`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    const maxPlayers = data.maxPlayers;
                    setPlayerNames(Array(maxPlayers).fill(''));
                    setPositions(generatePositions(maxPlayers)); // 포지션 초기화
                } else {
                    console.error('Failed to fetch game info');
                    alert('Failed to fetch game info');
                    navigate('/game/create');
                }
            } catch (error) {
                console.error('Error fetching game info:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchGameInfo();
    }, [gameId, navigate]);

    const handleInputChange = (index, value) => {
        const updatedNames = [...playerNames];
        updatedNames[index] = value;
        setPlayerNames(updatedNames);
    };

    const handleAddAllPlayers = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('You must log in to add players.');
            navigate('/login');
            return;
        }

        for (let i = 0; i < playerNames.length; i++) {
            const name = playerNames[i] || positions[i]; // 이름이 없으면 포지션 이름 사용
            console.log(`Adding player with name: ${name} for position: ${positions[i]}`); // 디버깅 로그

            try {
                const response = await fetch('http://localhost:8080/api/players/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify({ gameId: parseInt(gameId, 10), playerName: name }),
                });

                if (!response.ok) {
                    console.error(`Failed to add player ${name}`);
                    alert(`Failed to add player: ${name}`);
                }
            } catch (error) {
                console.error(`Error adding player ${name}:`, error);
                alert(`Error adding player: ${name}`);
            }
        }

        alert('All players added successfully!');
        navigate(`/game/${gameId}/betting`); // 플레이어 추가 완료 후 베팅 페이지로 이동
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="player-add-container">
            <h2>Add Players to Game ID: {gameId}</h2>
            <div className="player-grid">
                {playerNames.map((name, index) => (
                    <label key={index}>
                        {positions[index]}:
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => handleInputChange(index, e.target.value)}
                        />
                    </label>
                ))}
            </div>
            <button className="add-players-button" onClick={handleAddAllPlayers}>
                Add All Players
            </button>
        </div>
    );
}

export default PlayerAddForm;