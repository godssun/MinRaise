package com.github.minraise.service.user;

import com.github.minraise.dto.User.LoginRequest;
import com.github.minraise.dto.User.LoginResponse;
import com.github.minraise.dto.User.SignUpRequest;
import com.github.minraise.dto.User.SignUpResponse;

public interface UserService {
	SignUpResponse registerUser(SignUpRequest signUpRequest);


	LoginResponse loginUser(LoginRequest loginRequest);
}
