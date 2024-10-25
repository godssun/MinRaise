import React, { useState } from 'react';
import './GameCreate.css'; // 게임 생성 페이지 전용 스타일
import PlayerAdd from './PlayerAdd';  // PlayerAdd를 불러옴

function GameCreate() {
    const [smallBlind, setSmallBlind] = useState('');
    const [bigBlind, setBigBlind] = useState('');
    const [gameId, setGameId] = useState(null); // gameId 상태 추가

    const handleCreateGame = async (event) => {
        event.preventDefault();

        // Max Players를 9로 고정
        const maxPlayers = 9;

        const response = await fetch('http://localhost:8080/api/games/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ smallBlind, bigBlind, maxPlayers })
        });

        const data = await response.json();
        if (response.ok) {
            console.log('Game Creation Success:', data);
            setGameId(data.gameId); // 서버에서 gameId를 받아와서 상태에 저장
            alert('Game created successfully!');
        } else {
            console.log('Game Creation Error:', data);
            alert('Game creation failed!');
        }
    };

    return (
        <div className="game-create-container">
            <h2>Create Game</h2>
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
                {/* 게임 9플레이어 기준 설명 추가 */}
                <p className="game-info">게임은 9플레이어 기준입니다.</p>
                <button type="submit">Create Game</button>
            </form>

            {/* 게임이 생성되면 PlayerAdd 컴포넌트를 보여주고, gameId를 전달 */}
            {gameId && <PlayerAdd gameId={gameId} />}
        </div>
    );
}

export default GameCreate;