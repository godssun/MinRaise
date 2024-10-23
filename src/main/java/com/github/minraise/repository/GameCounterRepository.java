package com.github.minraise.repository;

import com.github.minraise.entity.game.GameCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameCounterRepository extends JpaRepository<GameCounter, Long> {
}
