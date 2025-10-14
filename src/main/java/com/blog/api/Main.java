package com.blog.api;

import com.blog.api.security.services.JwtAuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication //Autoconfiguration Component Scanning Tomcat Server
public class Main {
    static void main() {
        SpringApplication.run(Main.class);
    }
}
