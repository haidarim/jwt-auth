/*
 * Copyright (c) 2026 Haidarim
 * All rights reserved.
 *
 * This software is proprietary and confidential.
 * Unauthorized use, copying, modification, or distribution of this
 * software, in whole or in part, is strictly prohibited without
 * prior written permission from the author.
 */

package io.github.haidarim.service;

import io.github.haidarim.entity.TestUser;

public interface TestUserService {

    TestUser createOrUpdateUser(String username, String email, String password,
                                String uniqueNumber, String salt);

    void deleteUser(String email);

}
