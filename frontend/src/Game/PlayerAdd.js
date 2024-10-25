import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import './PlayerAdd.css'; // 스타일 파일

function PlayerAdd() {
    const initialPositions = ['UTG', 'UTG+1', 'UTG+2', 'MP', 'MP+1', 'CO', 'BTN', 'SB', 'BB']; // 포지션 정의
    const [players, setPlayers] = useState(Array(9).fill(''));
    const { gameId } = useParams(); // URL에서 gameId를 가져옴
    const navigate = useNavigate();

    useEffect(() => {
        // 게임 생성 후 자동으로 플레이어를 추가할 수 있게 설정 가능
    }, []);

    const handlePlayerChange = (index, value) => {
        const updatedPlayers = [...players];
        updatedPlayers[index] = value;
        setPlayers(updatedPlayers);
    };

    const handleAddPlayers = async (event) => {
        event.preventDefault();

        for (let i = 0; i < players.length; i++) {
            const playerName = players[i] || initialPositions[i]; // 이름이 입력되지 않으면 포지션이 기본 이름으로 설정됨
            try {
                const response = await fetch('http://localhost:8080/api/players/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ gameId, playername: playerName })
                });

                if (!response.ok) {
                    console.log(`Error adding player ${i + 1}`);
                    return;
                }
            } catch (error) {
                console.error(`Error adding player ${i + 1}:`, error);
            }
        }

        alert('플레이어 추가 완료!');
        navigate(`/betting/${gameId}`); // 베팅 페이지로 이동
    };

    return (
        <div className="player-add-container">
            <h2>플레이어 추가</h2>
            <form onSubmit={handleAddPlayers}>
                {players.map((player, index) => (
                    <label key={index}>
                        {initialPositions[index]}: {/* 포지션을 표시 */}
                        <input
                            type="text"
                            value={player}
                            onChange={(e) => handlePlayerChange(index, e.target.value)}
                            placeholder="플레이어 이름을 입력해주세요." // placeholder 수정
                        />
                    </label>
                ))}
                <button type="submit">플레이어 추가</button>
            </form>
        </div>
    );
}

export default PlayerAdd;