package com.hfing.userservice.configuration;

import com.hfing.userservice.common.RoleType;
import com.hfing.userservice.entity.Role;
import com.hfing.userservice.entity.User;
import com.hfing.userservice.repository.RoleRepository;
import com.hfing.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "USER-INIT-DATA")
public class AppInitialDataConfiguration implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "12345678";

    private static final String SELLER_EMAIL = "seller@gmail.com";
    private static final String SELLER_PASSWORD = "12345678";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull ... args) {
        if(!userRepository.existsByEmail(ADMIN_EMAIL)) {
            Role adminRole = roleRepository.findByName(RoleType.ADMIN.name())
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(RoleType.ADMIN.name())
                            .description("Admin role")
                            .build()));

            User user = User.builder()
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .build();

            user.addRole(adminRole);
            userRepository.save(user);
        }

        if(!userRepository.existsByEmail(SELLER_EMAIL)) {
            Role adminRole = roleRepository.findByName(RoleType.SELLER.name())
                    .orElseGet(() -> roleRepository.save(Role.builder()
                            .name(RoleType.SELLER.name())
                            .description("Seller role")
                            .build()));

            User user = User.builder()
                    .email(SELLER_EMAIL)
                    .password(passwordEncoder.encode(SELLER_PASSWORD))
                    .build();

            user.addRole(adminRole);
            userRepository.save(user);
        }

        log.info("Initial data created successfully");
    }
}