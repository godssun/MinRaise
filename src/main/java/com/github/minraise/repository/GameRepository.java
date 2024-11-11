package com.github.minraise.repository;

import com.github.minraise.entity.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface GameRepository extends JpaRepository<Game, Long> {
	List<Game> findByUser_UserId(Long userId);

	// 기본적인 JPA CRUD 메소드들을 자동으로 제공
}
