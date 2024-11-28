import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './GameCreationForm.css';

function GameCreationForm() {
    const [smallBlind, setSmallBlind] = useState('');
    const [bigBlind, setBigBlind] = useState('');
    const [maxPlayers, setMaxPlayers] = useState('');
    const navigate = useNavigate();

    const handleCreateGame = async (event) => {
        event.preventDefault();

        const token = localStorage.getItem('token');
        if (!token) {
            alert('You must log in to create a game.');
            navigate('/login');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/games/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    smallBlind: parseInt(smallBlind, 10),
                    bigBlind: parseInt(bigBlind, 10),
                    maxPlayers: parseInt(maxPlayers, 10),
                }),
            });

            if (response.ok) {
                const data = await response.json();
                console.log('Game created successfully:', data);
                alert(`Game created with ID: ${data.gameId}`);
                // 생성된 게임 ID를 기반으로 플레이어 추가 페이지로 리다이렉트
                navigate(`/game/${data.gameId}/add-players`);
            } else {
                const errorData = await response.json();
                console.error('Error creating game:', errorData);
                alert(`Failed to create game: ${errorData.message || 'Unknown error'}`);
            }
        } catch (error) {
            console.error('Request failed:', error);
            alert('Failed to create game. Please try again.');
        }
    };

    return (
        <div className="game-creation-container">
            <h2>Create a Game</h2>
            <form onSubmit={handleCreateGame}>
                <label>
                    Small Blind:
                    <input
                        type="number"
                        value={smallBlind}
                        onChange={(e) => setSmallBlind(e.target.value)}
                        required
                    />
                </label>
                <label>
                    Big Blind:
                    <input
                        type="number"
                        value={bigBlind}
                        onChange={(e) => setBigBlind(e.target.value)}
                        required
                    />
                </label>
                <label>
                    Max Players:
                    <input
                        type="number"
                        value={maxPlayers}
                        onChange={(e) => setMaxPlayers(e.target.value)}
                        required
                    />
                </label>
                <button type="submit">Create Game</button>
            </form>
        </div>
    );
}

export default GameCreationForm;