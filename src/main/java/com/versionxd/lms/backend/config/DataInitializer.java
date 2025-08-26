package com.versionxd.lms.backend.config;

import com.versionxd.lms.backend.model.SystemRole;
import com.versionxd.lms.backend.model.SystemRoleName;
import com.versionxd.lms.backend.repository.SystemRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SystemRoleRepository systemRoleRepository;

    @Autowired
    public DataInitializer(SystemRoleRepository systemRoleRepository) {
        this.systemRoleRepository = systemRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if ROLE_USER exists, if not, create it
        if (systemRoleRepository.findByName(SystemRoleName.ROLE_USER).isEmpty()) {
            SystemRole userRole = new SystemRole();
            userRole.setName(SystemRoleName.ROLE_USER);
            systemRoleRepository.save(userRole);
            System.out.println("Initialized ROLE_USER");
        }

        // Check if ROLE_ADMIN exists, if not, create it
        if (systemRoleRepository.findByName(SystemRoleName.ROLE_ADMIN).isEmpty()) {
            SystemRole adminRole = new SystemRole();
            adminRole.setName(SystemRoleName.ROLE_ADMIN);
            systemRoleRepository.save(adminRole);
            System.out.println("Initialized ROLE_ADMIN");
        }
    }
}