package io.github.haidarim.common;

import io.github.haidarim.entity.TestUser;
import io.github.haidarim.repository.TestUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import static org.junit.jupiter.api.Assertions.*;

@TestComponent
public class JwtTestHelper {

    @Autowired
    TestUserRepository userRepository;


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
}
