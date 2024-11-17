package com.github.minraise.dto.bet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BetRequest {
	private Long gameId;
	private Integer playerIndex;
	private BigDecimal betAmount;

}
