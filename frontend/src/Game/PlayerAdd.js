import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './PlayerAdd.css'; // 플레이어 추가 페이지 전용 스타일

function PlayerAdd({ gameId }) {
    const [players, setPlayers] = useState(Array(9).fill(''));
    const navigate = useNavigate();

    const handlePlayerChange = (index, value) => {
        const updatedPlayers = [...players];
        updatedPlayers[index] = value;
        setPlayers(updatedPlayers);
    };

    const handleAddPlayers = async (event) => {
        event.preventDefault();

        let addedPlayers = [];
        for (let i = 0; i < players.length; i++) {
            const playername = players[i] || `Player ${i + 1}`; // 이름이 없으면 기본 이름으로 설정

            const response = await fetch('http://localhost:8080/api/players/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ gameId, playername })
            });

            const data = await response.json();
            if (!response.ok) {
                console.log(`Error adding player ${i + 1}:`, data);
                alert('Player addition failed!');
                return;
            }

            addedPlayers.push(playername); // 성공적으로 추가된 플레이어 이름 저장
        }


    };

    const getPosition = (index) => {
        // 9링 포지션에 따라 포지션 이름 설정
        const positions = ["UTG", "UTG+1", "UTG+2", "MP", "MP+1", "CO", "BTN", "SB", "BB"];
        return positions[index];
    };

    return (
        <div className="player-add-container">
            <h2>Add Players</h2>
            <form onSubmit={handleAddPlayers}>
                {players.map((player, index) => (
                    <label key={index}>
                        Player {index + 1} ({getPosition(index)}):
                        <input
                            type="text"
                            value={player}
                            onChange={(e) => handlePlayerChange(index, e.target.value)}
                        />
                    </label>
                ))}
                <button type="submit">Add Players</button>
            </form>
        </div>
    );
}

export default PlayerAdd;