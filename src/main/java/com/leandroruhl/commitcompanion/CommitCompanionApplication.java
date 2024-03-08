package com.leandroruhl.commitcompanion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CommitCompanionApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommitCompanionApplication.class, args);
    }

}
