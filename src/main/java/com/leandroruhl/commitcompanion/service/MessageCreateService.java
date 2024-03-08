package com.leandroruhl.commitcompanion.service;

import com.leandroruhl.commitcompanion.listeners.EventListener;
import com.leandroruhl.commitcompanion.listeners.MessageListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageCreateService extends MessageListener implements EventListener<MessageCreateEvent> {

    public MessageCreateService(DiscordBotInstanceService discordBotInstanceService) {
        super(discordBotInstanceService);
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(final MessageCreateEvent event) {
        return processMessage(event.getMessage());
    }
}
