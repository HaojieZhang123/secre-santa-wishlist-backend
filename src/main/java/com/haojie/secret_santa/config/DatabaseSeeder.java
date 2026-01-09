package com.haojie.secret_santa.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.haojie.secret_santa.model.auth.ERole;
import com.haojie.secret_santa.model.auth.Role;
import com.haojie.secret_santa.model.auth.User;
import com.haojie.secret_santa.repository.auth.RoleRepository;
import com.haojie.secret_santa.repository.auth.UserRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedUsers();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_USER));
        }
    }

    private void seedUsers() {
        // 1. haojie (All roles)
        if (!userRepository.existsByUsername("haojie")) {
            User user = new User("haojie", passwordEncoder.encode("Haojie2002"));
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow());
            roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow());
            user.setRoles(roles);
            userRepository.save(user);
        }

        // 2. admin (All roles)
        if (!userRepository.existsByUsername("admin")) {
            User user = new User("admin", passwordEncoder.encode("admin123"));
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow());
            roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow());
            user.setRoles(roles);
            userRepository.save(user);
        }

        // 3. user (USER)
        if (!userRepository.existsByUsername("user")) {
            User user = new User("user", passwordEncoder.encode("12345678"));
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow());
            user.setRoles(roles);
            userRepository.save(user);
        }
    }

}
