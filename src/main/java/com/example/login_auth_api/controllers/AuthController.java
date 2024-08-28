package com.example.login_auth_api.controllers;

import com.example.login_auth_api.domain.user.Users;
import com.example.login_auth_api.dto.LoginRequestDTO;
import com.example.login_auth_api.dto.RegisterRequestDTO;
import com.example.login_auth_api.dto.ResponseDTO;
import com.example.login_auth_api.infra.security.TokenService;
import com.example.login_auth_api.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;

	@PostMapping("/login")
	public ResponseEntity login(@RequestBody LoginRequestDTO loginDTO){
		Users user = this.userRepository.findByEmail(loginDTO.email()).orElseThrow(() -> new RuntimeException("User not found"));
		if(passwordEncoder.matches(loginDTO.password(), user.getPassword())){
			String token = this.tokenService.generateToken(user);
			return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
		}
		return ResponseEntity.badRequest().build();
	}

	@PostMapping("/register")
	public ResponseEntity register(@RequestBody RegisterRequestDTO registerDTO){
		Optional<Users> user = this.userRepository.findByEmail(registerDTO.email());

		if(user.isEmpty()){
			Users newUser = Users.builder()
					.name(registerDTO.name())
					.password(passwordEncoder.encode(registerDTO.password()))
					.email(registerDTO.email())
					.build();


			this.userRepository.save(newUser);

			String token = this.tokenService.generateToken(newUser);
			return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
		}

		return ResponseEntity.badRequest().build();
	}
}
