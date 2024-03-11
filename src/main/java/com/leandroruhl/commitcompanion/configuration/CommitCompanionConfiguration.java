package com.leandroruhl.commitcompanion.configuration;

import com.leandroruhl.commitcompanion.listeners.EventListener;
import discord4j.common.ReactorResources;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.rest.request.RouteMatcher;
import discord4j.rest.response.ResponseFunction;
import io.netty.channel.unix.Errors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;


@Configuration
public class CommitCompanionConfiguration {
    @Value("${discord.token}")
    private String token;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(final List<EventListener<T>> eventListeners) {
        final GatewayDiscordClient client = DiscordClient.builder(token)
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
