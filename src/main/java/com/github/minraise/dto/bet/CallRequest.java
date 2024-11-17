package com.github.minraise.dto.bet;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallRequest {
	private Long gameId;
	private int playerIndex;
}
