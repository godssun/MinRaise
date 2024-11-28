import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './BettingForm.css';

function BettingForm() {
    const { gameId } = useParams();
    const navigate = useNavigate();
    const [players, setPlayers] = useState([]);
    const [betAmounts, setBetAmounts] = useState({});
    const [disabledPlayers, setDisabledPlayers] = useState({});
    const [actionLogs, setActionLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [smallBlind, setSmallBlind] = useState(0);
    const [bigBlind, setBigBlind] = useState(0);
    const [currentTurn, setCurrentTurn] = useState(null); // 초기값을 null로 설정

    useEffect(() => {
        const fetchGameDetails = async () => {
            const token = localStorage.getItem('token');
            if (!token) {
                alert('You must log in to access this page.');
                navigate('/login');
                return;
            }

            try {
                // Fetch game details to get small and big blind amounts
                const gameResponse = await fetch(`http://localhost:8080/api/games/${gameId}`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (gameResponse.ok) {
                    const gameData = await gameResponse.json();
                    setSmallBlind(gameData.smallBlind);
                    setBigBlind(gameData.bigBlind);

                    // Fetch players data
                    const playersResponse = await fetch(`http://localhost:8080/api/players/game/${gameId}`, {
                        method: 'GET',
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    });

                    if (playersResponse.ok) {
                        const data = await playersResponse.json();
                        setPlayers(data);

                        setBetAmounts(
                            data.reduce((acc, player) => {
                                if (player.position === 'SB') {
                                    acc[player.playerIndex] = gameData.smallBlind;
                                } else if (player.position === 'BB') {
                                    acc[player.playerIndex] = gameData.bigBlind;
                                } else {
                                    acc[player.playerIndex] = '';
                                }
                                return acc;
                            }, {})
                        );

                        // UTG 인덱스를 찾고 현재 턴을 설정
                        const utgIndex = data.findIndex((player) => player.position === 'UTG');
                        setCurrentTurn(utgIndex); // UTG가 첫 번째 턴이 됨
                    } else {
                        console.error('Failed to fetch players');
                    }
                } else {
                    console.error('Failed to fetch game details');
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchGameDetails();
    }, [gameId, navigate]);

    const handleInputChange = (playerIndex, value) => {
        setBetAmounts({
            ...betAmounts,
            [playerIndex]: value,
        });
    };

    const handleAction = async (playerIndex, action) => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('You must log in to perform this action.');
            navigate('/login');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/bets/${action.toLowerCase()}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    gameId: parseInt(gameId, 10),
                    playerIndex,
                }),
            });

            if (response.ok) {
                const actionLog = `${players[playerIndex].playerName} performed ${action}`;
                setActionLogs((prevLogs) => [...prevLogs, actionLog]);

                // 턴 이동: 다음 플레이어로 이동
                setCurrentTurn((prevTurn) => (prevTurn + 1) % players.length); // 순환적으로 돌아감
            } else {
                alert(`Failed to perform ${action}`);
            }
        } catch (error) {
            console.error(`Error performing ${action} action:`, error);
        }
    };

    const handleBet = async (playerIndex) => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('You must log in to place a bet.');
            navigate('/login');
            return;
        }

        const betAmount = parseInt(betAmounts[playerIndex], 10);

        try {
            const response = await fetch('http://localhost:8080/api/bets/place', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    gameId: parseInt(gameId, 10),
                    playerIndex,
                    betAmount,
                }),
            });

            const data = await response.json();

            if (response.ok && data.valid) {
                const actionLog = `${players[playerIndex].playerName} raised ${data.betAmount} (Raise Amount: ${data.raiseAmount})`;
                setActionLogs((prevLogs) => [...prevLogs, actionLog]);

                setBetAmounts({ ...betAmounts, [playerIndex]: '' });

                // 턴 이동
                setCurrentTurn((prevTurn) => (prevTurn + 1) % players.length); // 순환적으로 돌아감
            } else {
                setActionLogs((prevLogs) => [
                    ...prevLogs,
                    `Minimum raise violated by ${players[playerIndex].playerName} (${players[playerIndex].position}). Required bet amount is ${data.requiredBetAmount}.`,
                ]);
                alert(`Invalid bet: Minimum required bet is ${data.requiredBetAmount}`);
            }
        } catch (error) {
            console.error('Error placing bet:', error);
            alert('An error occurred while placing the bet.');
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="betting-container">
            <h2>Betting for Game ID: {gameId}</h2>
            <div className="players-list">
                {players.map((player, index) => (
                    <div key={player.playerIndex} className="player-item">
                        <span>
                            {player.playerName} ({player.position})
                        </span>
                        <input
                            type="number"
                            placeholder={`Enter bet amount`}
                            value={betAmounts[player.playerIndex]}
                            onChange={(e) => handleInputChange(player.playerIndex, e.target.value)}
                            disabled={currentTurn !== index} // 현재 턴인 플레이어만 활성화
                        />
                        <div className="action-buttons">
                            <button
                                onClick={() => handleBet(player.playerIndex)}
                                disabled={currentTurn !== index} // 현재 턴인 플레이어만 버튼 활성화
                            >
                                Bet
                            </button>
                            <button
                                onClick={() => handleAction(player.playerIndex, 'Call')}
                                disabled={currentTurn !== index} // 현재 턴인 플레이어만 버튼 활성화
                            >
                                Call
                            </button>
                            <button
                                onClick={() => handleAction(player.playerIndex, 'Fold')}
                                disabled={currentTurn !== index} // 현재 턴인 플레이어만 버튼 활성화
                            >
                                Fold
                            </button>
                        </div>
                    </div>
                ))}
            </div>
            <div className="action-logs">
                <h3>Action Logs</h3>
                <ul>
                    {actionLogs.map((log, index) => (
                        <li key={index}>{log}</li>
                    ))}
                </ul>
            </div>
        </div>
    );
}

export default BettingForm;