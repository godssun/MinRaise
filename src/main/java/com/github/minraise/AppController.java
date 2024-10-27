package com.github.minraise;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "CI/CD TEST", description = "CI/CD TEST 관련 API")
public class AppController {

	@GetMapping("/")
	public String home() {
		return "Now on Spring Boot????!!!!!!";
	}
}
