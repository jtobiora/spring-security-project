package com.security.demo.utils;

import com.security.demo.model.Role;
import com.security.demo.model.RoleName;
import com.security.demo.model.User;
import com.security.demo.repo.RoleRepository;
import com.security.demo.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener
    public void seedDatabase(ContextRefreshedEvent event) {
        // Check if the database is already seeded
        if (databaseIsSeeded()) {
            //save role
           Role adminRole = Role.builder().name(RoleName.ROLE_ADMIN).build();
           Role userRole = Role.builder().name(RoleName.ROLE_USER).build();

           roleRepository.saveAll(Arrays.asList(adminRole, userRole));


           //set up default admin user
           User adminUser  = User.builder()
                   .email("admin@gmail.com")
                   .password(passwordEncoder.encode("admin123"))
                   .username("admin")
                   .name("Admin")
                   .roles(Collections.singleton(adminRole))
                       .build();

           userRepository.save(adminUser);
        }
    }

    private boolean databaseIsSeeded() {
        // Implement logic to check if the database is already seeded
       return roleRepository.findAll().isEmpty();
    }

}
