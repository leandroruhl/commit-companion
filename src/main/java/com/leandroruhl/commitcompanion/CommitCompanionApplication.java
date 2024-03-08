package com.leandroruhl.commitcompanion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class CommitCompanionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommitCompanionApplication.class, args);
    }

}
