package com.github.minraise.entity.game;

import com.github.minraise.entity.bet.Bet;
import com.github.minraise.entity.player.Player;
import com.github.minraise.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games")
public class Game {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_id")
	private Long gameId;

	private BigDecimal smallBlind;
	private BigDecimal bigBlind;
	private int maxPlayers;
	private BigDecimal currentBetAmount;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	private List<Player> players = new ArrayList<>();

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	private List<Bet> bets = new ArrayList<>();
}
