package com.versionxd.lms.backend;

import com.versionxd.lms.backend.service.SocketIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    @Autowired
    private SocketIOService socketIOService;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            socketIOService.startServer();
        };
    }

}
