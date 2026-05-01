package io.github.haidarim.service;

import io.github.haidarim.entity.TestUser;

public interface TestUserService {

    TestUser createOrUpdateUser(String username, String email, String password,
                                String uniqueNumber, String salt);

    void deleteUser(String email);

}
