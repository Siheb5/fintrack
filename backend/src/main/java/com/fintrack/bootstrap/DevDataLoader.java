package com.fintrack.bootstrap;

import com.fintrack.user.User;
import com.fintrack.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TEMPORARY (step 3c): seeds one test user at startup so the transaction
 * endpoints have an owner to attach rows to, before real login exists.
 * Removed/replaced when authentication arrives in step 3d.
 *
 * @Profile("!test") = do NOT run during automated tests (they seed their own data).
 */
@Component
@Profile("!test")
public class DevDataLoader implements CommandLineRunner {

    /** The fixed id the controller uses as the "current user" for now. */
    public static final Long DEV_USER_ID = 1L;

    private final UserRepository users;

    public DevDataLoader(UserRepository users) {
        this.users = users;
    }

    @Override
    public void run(String... args) {
        if (!users.existsByEmail("dev@fintrack.local")) {
            users.save(new User("dev@fintrack.local", "not-a-real-password"));
        }
    }
}
