package com.github.minraise;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

	@GetMapping("/")
	public String home() {
		return "Now on Spring Boot!";
	}
}
