package io.github.haidarim.controller;

import io.github.haidarim.api.dto.request.AuthenticationRequest;
import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.api.service.JwtAuthenticationService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/auth")
@RequiredArgsConstructor
public class TestAuthenticationController {

    private final JwtAuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<@NotNull AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        }catch (Exception e){
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).build();
        }
    }

    @GetMapping("/get/hello-message")
    public String getHelloMessage(){
        return "Hello!";
    }
}
