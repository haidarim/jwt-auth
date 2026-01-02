package io.github.haidarim.common;

import io.github.haidarim.api.dto.request.AuthenticationRequest;
import io.github.haidarim.api.dto.request.RegisterRequest;
import io.github.haidarim.api.dto.response.AuthenticationResponse;
import io.github.haidarim.api.service.JwtAuthenticationService;
import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.entity.TestUser;
import io.github.haidarim.impl.config.JwtConfig;
import io.github.haidarim.impl.service.DefaultJwtService;
import io.github.haidarim.repository.TestUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@RequiredArgsConstructor
@TestConfiguration(proxyBeanMethods = false)
public class TestAppConfig {

    private final TestUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsService userDetailsService(){
        return userRepository::findByEmail;
    }

    @Bean
    public JwtAuthenticationService jwtAuthenticationService(JwtService jwtService, AuthenticationManager authenticationManager){
        return new JwtAuthenticationService() {
            @Override
            public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception{
                String email = request.getEmail() != null && !request.getEmail().trim().isEmpty()? request.getEmail()
                        : request.getUsername() != null && !request.getUsername().trim().isEmpty()? userRepository.findEmailByUsername(request.getUsername())
                        : request.getUniqueNumber() != null && !request.getUniqueNumber().trim().isEmpty()? userRepository.findEmailByUniqueNumber(request.getUniqueNumber())
                        :"";
                if (email.isEmpty()){
                    throw new RuntimeException("Email not provided");
                }
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.getPassword()
                        )
                );

                Optional<TestUser> user = Optional.of(userRepository.findByEmail(email));
                String token = jwtService.createToken(user.get().getEmail());
                return AuthenticationResponse
                        .builder()
                        .token(token)
                        .build();
            }

            @Override
            public AuthenticationResponse register(RegisterRequest request) throws Exception {
                TestUser user = TestUser
                        .builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .passwordHash(passwordEncoder.encode(request.getPassword()))
                        .uniqueNumber(request.getUniqueNumber())
                        .salt("DUMMY_VALUE")
                        .build();
                userRepository.save(user);
                String token = jwtService.createToken(user.getEmail());
                return AuthenticationResponse
                        .builder()
                        .token(token)
                        .build();
            }
        };
    }

//    @Bean
//    public JwtService jwtService(JwtConfig jwtConfig){
//        return new DefaultJwtService(jwtConfig);
//    }

    @Bean
    public JwtConfig jwtConfig(
            @Value("${jwt.algorithm}") String algorithm,
            @Value("${jwt.hs_secret}") String hsSecret,
            @Value("${jwt.pr_secret}") String privateKey,
            @Value("${jwt.pub_secret}") String publicKey,
            @Value("${jwt.check_expiration}") boolean checkExpiration,
            @Value("${jwt.expiration_time}") long expirationMillis
    ) {
        return new JwtConfig(
                algorithm,
                hsSecret,
                privateKey,
                publicKey,
                checkExpiration,
                expirationMillis
        );
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            // trivial auth for testing
            return authentication;
        };
    }

}
