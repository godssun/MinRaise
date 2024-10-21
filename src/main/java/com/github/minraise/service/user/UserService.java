package com.github.minraise.service.user;

import com.github.minraise.dto.User.LoginRequestDTO;
import com.github.minraise.dto.User.UserDTO;
import com.github.minraise.entity.user.User;

public interface UserService {
	User registerUser(UserDTO userDto);


	String loginUser(LoginRequestDTO loginRequest);
}
