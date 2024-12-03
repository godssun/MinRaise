import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './BettingForm.css';

function BettingForm() {
    const { gameId } = useParams();
    const navigate = useNavigate();
    const [players, setPlayers] = useState([]);
    const [betAmounts, setBetAmounts] = useState({});
    const [actionLogs, setActionLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [smallBlind, setSmallBlind] = useState(0);
    const [bigBlind, setBigBlind] = useState(0);
    const [currentTurn, setCurrentTurn] = useState(null);
    const [currentRound, setCurrentRound] = useState(null);

    useEffect(() => {
        const fetchGameDetails = async () => {
            const token = localStorage.getItem('token');
            if (!token) {
                alert('You must log in to access this page.');
                navigate('/login');
                return;
            }

            try {
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
                    setCurrentRound(gameData.currentRound);

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

                        const utgIndex = data.findIndex((player) => player.position === 'UTG');
                        setCurrentTurn(utgIndex);
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
                const actionLog = `${players[playerIndex]?.playerName || 'Player'} performed ${action}`;
                setActionLogs((prevLogs) => [...prevLogs, actionLog]);

                if (action === 'Fold') {
                    setPlayers((prevPlayers) =>
                        prevPlayers.map((player) =>
                            player.playerIndex === playerIndex ? { ...player, isFolded: true } : player
                        )
                    );
                }

                updateNextTurn();
                checkRoundOver();
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
                const actionLog = `${players[playerIndex]?.playerName || 'Player'} raised ${data.betAmount} (Raise Amount: ${data.raiseAmount})`;
                setActionLogs((prevLogs) => [...prevLogs, actionLog]);

                setBetAmounts({ ...betAmounts, [playerIndex]: '' });

                updateNextTurn();
                checkRoundOver();
            } else {
                alert(`Invalid bet: Minimum required bet is ${data.requiredBetAmount}`);
            }
        } catch (error) {
            console.error('Error placing bet:', error);
        }
    };

    const updateNextTurn = () => {
        setCurrentTurn((prevTurn) => {
            let nextTurn = (prevTurn + 1) % players.length;

            while (players[nextTurn]?.isFolded) {
                nextTurn = (nextTurn + 1) % players.length;

                if (nextTurn === prevTurn) {
                    console.error("No active players left to take a turn.");
                    return prevTurn;
                }
            }

            console.log("Next Turn:", nextTurn, "Player:", players[nextTurn]);
            return nextTurn;
        });
    };

    const checkRoundOver = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('You must log in to perform this action.');
            navigate('/login');
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/games/${gameId}/round-status`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                const data = await response.json();
                setCurrentRound(data.nextRound);

                if (data.isRoundOver) {
                    alert(`Round has ended. Proceeding to ${data.nextRound} round.`);
                    setTimeout(() => {
                        window.location.reload();
                    }, 500); // 새로고침으로 다음 라운드 데이터를 로드
                }
            } else {
                alert('Failed to check round status.');
            }
        } catch (error) {
            console.error('Error checking round status:', error);
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="betting-container">
            <h2>Betting for Game ID: {gameId}</h2>
            <h3>Current Round: {currentRound || 'Loading...'}</h3>
            <div className="players-list">
                {players.map((player, index) => (
                    <div
                        key={player.playerIndex}
                        className={`player-item ${player.isFolded ? 'folded-player' : ''}`}
                    >
                        <span>
                            {player.playerName} ({player.position})
                        </span>
                        <input
                            type="number"
                            placeholder="Enter bet amount"
                            value={betAmounts[player.playerIndex] || ''}
                            onChange={(e) => handleInputChange(player.playerIndex, e.target.value)}
                            disabled={currentTurn !== index || player.isFolded}
                        />
                        <div className="action-buttons">
                            <button
                                onClick={() => handleBet(player.playerIndex)}
                                disabled={currentTurn !== index || player.isFolded}
                            >
                                Bet
                            </button>
                            <button
                                onClick={() => handleAction(player.playerIndex, 'Call')}
                                disabled={currentTurn !== index || player.isFolded}
                            >
                                Call
                            </button>
                            <button
                                onClick={() => handleAction(player.playerIndex, 'Fold')}
                                disabled={currentTurn !== index || player.isFolded}
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