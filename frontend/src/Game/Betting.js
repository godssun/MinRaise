import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './Betting.css'; // 스타일 파일

function Betting() {
    const { gameId } = useParams(); // URL에서 gameId를 가져옴
    const [players, setPlayers] = useState([]);
    const [betAmounts, setBetAmounts] = useState({}); // 각 플레이어의 베팅 금액을 저장

    useEffect(() => {
        // 게임에 속한 플레이어와 블라인드 정보를 서버에서 가져옴
        const fetchGameDetails = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/games/${gameId}`);
                const gameData = await response.json();
                setPlayers(gameData.players);

                // SB와 BB의 블라인드 금액을 초기값으로 설정
                const smallBlind = gameData.smallBlind || 0;
                const bigBlind = gameData.bigBlind || 0;

                setBetAmounts({
                    ...betAmounts,
                    [gameData.players.find(player => player.playerIndex === 8).playerId]: smallBlind, // SB
                    [gameData.players.find(player => player.playerIndex === 9).playerId]: bigBlind    // BB
                });
            } catch (error) {
                console.error("Error fetching game details:", error);
            }
        };

        fetchGameDetails();
    }, [gameId]);

    const handleBetChange = (playerId, amount) => {
        setBetAmounts({
            ...betAmounts,
            [playerId]: amount // 플레이어 ID별로 베팅 금액을 업데이트
        });
    };

    const handlePlaceBet = async (playerId, playerIndex, position) => {
        try {
            const betAmount = betAmounts[playerId] || 0; // 입력된 베팅 금액이 없으면 0으로 설정

            const response = await fetch('http://localhost:8080/api/bets/place', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    gameId,
                    playerIndex,
                    betAmount,
                    position
                })
            });

            if (!response.ok) {
                console.error("Bet failed:", await response.text());
                alert("베팅 실패!");
                return;
            }

            alert("베팅 성공!");
        } catch (error) {
            console.error("Error placing bet:", error);
        }
    };

    return (
        <div className="betting-container">
            <h2>베팅 페이지</h2>
            <table className="betting-table">
                <thead>
                <tr>
                    <th>플레이어</th>
                    <th>포지션</th>
                    <th>베팅 금액</th>
                    <th>액션</th>
                </tr>
                </thead>
                <tbody>
                {players.map((player, index) => (
                    <tr key={player.playerId}>
                        <td>{player.playerName}</td>
                        <td>{getPlayerPosition(player.playerIndex)}</td>
                        <td>
                            <input
                                type="number"
                                value={betAmounts[player.playerId] || ''}
                                onChange={(e) => handleBetChange(player.playerId, e.target.value)}
                                placeholder="베팅 금액 입력"
                            />
                        </td>
                        <td>
                            <button onClick={() => handlePlaceBet(player.playerId, player.playerIndex, getPlayerPosition(player.playerIndex))}>
                                베팅하기
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

// 포지션 인덱스 매핑 함수 (1부터 UTG로 시작)
const getPlayerPosition = (index) => {
    const positions = ["UTG", "UTG+1", "UTG+2", "MP", "MP+1", "CO", "BTN", "SB", "BB"];

    if (index >= 1 && index <= positions.length) {
        return positions[index - 1];  // 인덱스가 1일 때 UTG가 되도록
    }

    return 'Unknown'; // 그 외 값은 Unknown 처리
};

export default Betting;
