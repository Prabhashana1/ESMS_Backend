package lk.megasupply.esms_backend.service.impl;

import lk.megasupply.esms_backend.dto.AuthResponseDto;
import lk.megasupply.esms_backend.dto.LoginDto;
import lk.megasupply.esms_backend.repository.UserRepository;
import lk.megasupply.esms_backend.security.JwtService;
import lk.megasupply.esms_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto login(LoginDto loginDto) {
        // 1. Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        // 2. Find the user from DB
        var user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generate JWT token
        var jwtToken = jwtService.generateToken(user);

        // 4. Return response
        return AuthResponseDto.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}