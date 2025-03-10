package com.cse299.skillSphere;

import com.cse299.skillSphere.models.Role;
import com.cse299.skillSphere.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SkillSphereApplication {

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(SkillSphereApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            if (roleRepository.findByName("ROLE_INSTRUCTOR").isEmpty()) {
                Role ins = new Role();
                ins.setName("ROLE_INSTRUCTOR");
                roleRepository.save(ins);
            }
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                Role user = new Role();
                user.setName("ROLE_USER");
                roleRepository.save(user);
            }
        };
    }
}
