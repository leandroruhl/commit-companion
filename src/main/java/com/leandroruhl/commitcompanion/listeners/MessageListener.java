package com.leandroruhl.commitcompanion.listeners;

import com.leandroruhl.commitcompanion.model.DiscordBotInstance;
import com.leandroruhl.commitcompanion.service.DiscordBotInstanceService;
import com.leandroruhl.commitcompanion.service.RepoInfoService;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateMono;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class MessageListener {
    private final DiscordBotInstanceService discordBotService;
    private String channelId = "";
    private String serverId = "";
    private final Map<String, CommandHandler> commandMap = new HashMap<>();

    public MessageListener(DiscordBotInstanceService discordBotInstanceService) {
        this.discordBotService = discordBotInstanceService;
        commandMap.put("!setCommitCompanionChannel", this::handleSetChannel);
        commandMap.put("!watchRepository", this::handleWatchRepository);
    }

    public Mono<Void> processMessage(final Message eventMessage) {
        return Mono.just(eventMessage)
                .filter(message -> {
                    // isNotBot is also important to filter the bot's own messages
                    final Boolean isNotBot = message.getAuthor()
                            .map(user -> !user.isBot())
                            .orElse(false);

                    final Boolean isCommand = message.getContent().startsWith("!");

                    if (isNotBot && isCommand) {
                        this.channelId = message.getChannelId().asString();
                        message.getGuildId().ifPresent(guildId -> this.serverId = guildId.asString());
                    }

                    return isNotBot && isCommand;
                })
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    String content = eventMessage.getContent();

                    // Extract args from the message content
                    // args[0] is the command
                    String[] args = content.split("\\s+");

                    // Get the corresponding handler from the command map
                    CommandHandler handler = commandMap.get(args[0]);

                    if (handler != null) {
                        // Execute the handler for the args
                        return handler.handle(channel, content, args);
                    } else {
                        // Handle unknown args
                        return channel.createMessage("Unknown command: " + args[0]);
                    }
                })
                .then();
    }

    private MessageCreateMono handleSetChannel(MessageChannel channel, String content, String[] args) {
        try {
            DiscordBotInstance discordBotInstance = discordBotService.getBotInstanceByServerId(serverId);

            if (discordBotInstance != null) {
                discordBotService.changeBotInstanceChannel(discordBotInstance, channelId);
                return channel.createMessage("Channel updated successfully!");
            }

            discordBotService.createBotInstance(serverId, channelId);
            return channel.createMessage("Channel set successfully!");

        } catch (Exception e) {
            log.error(e.getMessage());
            return channel.createMessage("An error occurred while executing the set channel command");
        }
    }

    private MessageCreateMono handleWatchRepository(MessageChannel channel, String content, String[] args) {
        try {
            DiscordBotInstance bot = discordBotService.getBotInstanceByServerId(serverId);
            if (bot == null) {
                return channel.createMessage("Please set the bot channel first using the !setCommitCompanionChannel command");
            }
            discordBotService.addRepositoryToBotInstance(bot, args[1]);
            return channel.createMessage("Repository added successfully!");
        } catch (IllegalArgumentException e) {
            return channel.createMessage(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return channel.createMessage("An error occurred while executing the watch repository command");
        }
    }

    @FunctionalInterface
    private interface CommandHandler {
        MessageCreateMono handle(MessageChannel channel, String content, String[] args);
    }
}
