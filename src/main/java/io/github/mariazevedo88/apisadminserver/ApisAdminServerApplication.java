package io.github.mariazevedo88.apisadminserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@EnableAdminServer
@SpringBootApplication
public class ApisAdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApisAdminServerApplication.class, args);
	}

}
