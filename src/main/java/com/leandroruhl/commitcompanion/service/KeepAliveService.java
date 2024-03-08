package com.leandroruhl.commitcompanion.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeepAliveService {
    @Scheduled(fixedRate = 1 * 1000 * 60)
    public void keepAlive() {
        System.out.println(System.currentTimeMillis());
    }
}
