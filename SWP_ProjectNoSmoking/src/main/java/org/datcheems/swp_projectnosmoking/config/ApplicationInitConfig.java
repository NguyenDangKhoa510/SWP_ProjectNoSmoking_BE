package org.datcheems.swp_projectnosmoking.config;

import lombok.extern.slf4j.Slf4j;

import org.datcheems.swp_projectnosmoking.entity.Role;
import org.datcheems.swp_projectnosmoking.repository.RoleRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class ApplicationInitConfig {

    @Bean
    ApplicationRunner initRolesAndAdmin(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder // thêm vào đây
    ) {
        return args -> {
            log.info("Starting role initialization...");
            // 1. Tạo các role nếu chưa có
            for (Role.RoleName roleName : Role.RoleName.values()) {
                try {
                    Role existingRole = roleRepository.findByName(roleName)
                            .orElseGet(() -> {
                                log.info("Creating new role: {}", roleName);
                                Role role = new Role();
                                role.setName(roleName);
                                return roleRepository.save(role);
                            });
                    log.info("Role {} exists with id: {}", roleName, existingRole.getId());
                } catch (Exception e) {
                    log.error("Error initializing role {}: {}", roleName, e.getMessage());
                }
            }
            log.info("Role initialization completed");

            // 2. Tạo tài khoản admin nếu chưa có
            String adminUsername = "admin";
            if (!userRepository.existsByUsername(adminUsername)) {
                log.info("Creating default admin user...");

                Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                        .orElseThrow(() -> new RuntimeException("Admin role not found"));

                org.datcheems.swp_projectnosmoking.entity.User admin = new org.datcheems.swp_projectnosmoking.entity.User();
                admin.setUsername(adminUsername);
                admin.setEmail("admin@yourdomain.com");
                admin.setFullName("Administrator");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.getRoles().add(adminRole);

                userRepository.save(admin);
                log.info("Default admin user created with username 'admin' and password 'admin123'");
            } else {
                log.info("Admin user already exists");
            }
        };
    }
}
