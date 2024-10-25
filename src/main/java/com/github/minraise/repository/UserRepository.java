package com.github.minraise.repository;

import com.github.minraise.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
	Optional<User> findByUsername(String username);
	// 사용자 이름으로 사용자가 존재하는지 확인
	boolean existsByUsername(String username);
}
