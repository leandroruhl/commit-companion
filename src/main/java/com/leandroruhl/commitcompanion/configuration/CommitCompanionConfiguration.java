package com.leandroruhl.commitcompanion.configuration;

import com.leandroruhl.commitcompanion.listeners.EventListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class CommitCompanionConfiguration {
    @Value("${discord.token}")
    private String token;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(final List<EventListener<T>> eventListeners) {
        GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();

        for (final EventListener<T> eventListener : eventListeners) {
            client.on(eventListener.getEventType())
                    .flatMap(eventListener::execute)
                    .onErrorResume(eventListener::handleError)
                    .subscribe();
        }

        return client;
    }
}
