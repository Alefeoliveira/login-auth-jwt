package com.example.login_auth_api.repositories;

import com.example.login_auth_api.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {

	Optional<Users> findByEmail(String email);
}
