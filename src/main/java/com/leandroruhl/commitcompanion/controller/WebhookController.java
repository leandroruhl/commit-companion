package com.leandroruhl.commitcompanion.controller;

import com.leandroruhl.commitcompanion.service.DiscordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class WebhookController {
    DiscordService discordService;

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody String payload) {
        discordService.notifyCommits(payload);
    }
}