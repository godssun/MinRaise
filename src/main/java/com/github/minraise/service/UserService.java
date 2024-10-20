package com.github.minraise.service;

import com.github.minraise.dto.User.LoginRequestDTO;
import com.github.minraise.dto.User.UserDTO;
import com.github.minraise.entity.User;

public interface UserService {
	User registerUser(UserDTO userDto);


	String loginUser(LoginRequestDTO loginRequest);
}
