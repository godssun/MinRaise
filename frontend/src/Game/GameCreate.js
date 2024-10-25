import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './GameCreate.css'; // 스타일 파일

function GameCreate() {
    const [smallBlind, setSmallBlind] = useState('');
    const [bigBlind, setBigBlind] = useState('');
    const navigate = useNavigate();

    const handleCreateGame = async (event) => {
        event.preventDefault();

        const gameData = {
            smallBlind: parseFloat(smallBlind),
            bigBlind: parseFloat(bigBlind),
            maxPlayers: 9 // 9인 게임으로 고정
        };

        try {
            const response = await fetch('http://localhost:8080/api/games/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(gameData)
            });

            if (response.ok) {
                const game = await response.json();
                alert('게임이 생성되었습니다!');
                navigate(`/players/add/${game.gameId}`); // 플레이어 추가 페이지로 이동
            } else {
                alert('게임 생성에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error creating game:', error);
            alert('게임 생성 중 오류가 발생했습니다.');
        }
    };

    return (
        <div className="game-create-container">
            <h2>게임 생성</h2>
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
                <button type="submit">게임 생성</button>
            </form>
        </div>
    );
}

export default GameCreate;