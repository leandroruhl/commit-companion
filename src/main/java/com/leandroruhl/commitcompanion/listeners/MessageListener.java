package com.leandroruhl.commitcompanion.listeners;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class MessageListener {
    private String channelId = "";
    private String serverId = "";

    public Mono<Void> processMessage(final Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> {
                    final Boolean isNotBot = message.getAuthor()
                            .map(user -> !user.isBot())
                            .orElse(false);
                    if (isNotBot) {
                        this.channelId = message.getChannelId().asString();
                        message.getGuildId().ifPresent(guildId -> this.serverId = guildId.asString());
                    }
                    return isNotBot;
                })
                .flatMap(Message::getChannel)
                .flatMap(channel ->
                        channel.createMessage(String.format("Channel %s in server %s", channelId, serverId)))
                .then();
    }
}
