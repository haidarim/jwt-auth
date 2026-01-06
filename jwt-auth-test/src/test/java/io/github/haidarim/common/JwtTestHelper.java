package io.github.haidarim.common;

import io.github.haidarim.api.Role;
import io.github.haidarim.api.service.JwtService;
import io.github.haidarim.entity.TestUser;
import io.github.haidarim.impl.config.JwtConfig;
import io.github.haidarim.repository.TestUserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

@TestComponent
public class JwtTestHelper {

    @Autowired
    TestUserRepository userRepository;
    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    JwtService jwtService;
    @Autowired
    PasswordEncoder passwordEncoder;


    public void assertUserExistence(boolean shouldExist, String email){
        assertEquals(shouldExist, userRepository.findByEmail(email) != null);
    }

    public void removeTestUser(String email){
        TestUser user = userRepository.findByEmail(email);
        userRepository.delete(user);
        assertUserExistence(false, user.getEmail());
    }

    public void deleteAllTestUsers(){
        userRepository.deleteAll();
    }

    public void createTestUser(String username, String email, String password, String uniqueNumber){
        TestUser user = TestUser
                .builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .uniqueNumber(uniqueNumber)
                .salt("DUMMY_VALUE")
                .build();
        user.setRole(Role.USER);
        userRepository.save(user);
        assertUserExistence(true, email);
    }

    public long getJwtTimeoutMillis(){
        return jwtConfig.getExpirationMillis();
    }

    public void setJwtTimeoutMillis(long timeoutMillis){
        jwtConfig.setExpirationMillis(timeoutMillis);
    }

    public void setCheckExpiration(boolean checkExpiration){
        jwtConfig.setCheckExpiration(checkExpiration);
    }

    public void sleepUntil(BooleanSupplier condition, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();

        while (!condition.getAsBoolean()) {
            long remainingNanos = deadline - System.nanoTime();
            if (remainingNanos <= 0) {
                fail("Condition was not met within " + timeout);
            }

            try {
                // sleep min(25ms, remaining time)
                Thread.sleep(Math.min(25, remainingNanos / 1_000_000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Test interrupted");
            }
        }
    }

    public long getJwtExpirationTime(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return jwtService.getClaim(token, Claims::getExpiration).getTime();
    }
}
