package com.github.minraise.repository;

import com.github.minraise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
	Optional<User> findByUsername(String username);
}