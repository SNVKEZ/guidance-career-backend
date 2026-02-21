package org.ssu.belous.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssu.belous.dto.request.AuthorizeRequestDto;
import org.ssu.belous.security.JWTService;
import org.ssu.belous.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginPage(@RequestBody AuthorizeRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(requestDto.username(), requestDto.password());
        authenticationManager.authenticate(authToken);
        String token = jwtService.generateToken(requestDto.username(), userService.getRoleByUsername(requestDto.username()));
        log.info("Авторизация в " + LocalDateTime.now() + " пройдена для " + token);
        return ResponseEntity.ok(Collections.singletonMap("Authorization", token));
    }
}