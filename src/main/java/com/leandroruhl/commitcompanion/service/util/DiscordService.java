package com.leandroruhl.commitcompanion.service.util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leandroruhl.commitcompanion.model.DiscordBotInstance;
import com.leandroruhl.commitcompanion.model.RepoInfo;
import com.leandroruhl.commitcompanion.service.entities.DiscordBotInstanceService;
import com.leandroruhl.commitcompanion.service.entities.RepoInfoService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class DiscordService {
    private final GatewayDiscordClient client;
    private final DiscordBotInstanceService botInstanceService;
    private final RepoInfoService repoInfoService;
    private final Random random = new Random();
    private final Color[] predefinedColors = {
            Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
            Color.BLUE, Color.CYAN, Color.PINK, Color.VIVID_VIOLET,
            Color.SUMMER_SKY, Color.RUST, Color.MAGENTA
    };

    public void notifyCommits(String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);

            String pusherName = rootNode.path("pusher").path("name").asText();
            String repoId = rootNode.path("repository").path("id").asText();
            JsonNode commitsNode = rootNode.path("commits");

            RepoInfo repoInfo = repoInfoService.findById(Long.parseLong(repoId)).orElse(null);
            if (repoInfo == null) {
                log.warn("Received a webhook from a repository that is not found in DB");
                return;
            }

            List<DiscordBotInstance> discordBotInstances = botInstanceService.getBotInstancesByRepository(repoInfo);
            if (discordBotInstances.isEmpty()) {
                log.warn("No DiscordBotInstances found for this repository");
                return;
            }

            Color randomColor = predefinedColors[random.nextInt(predefinedColors.length)];

            for (JsonNode commitNode : commitsNode) {
                String message = commitNode.path("message").asText();
                String authorName = commitNode.path("author").path("name").asText();
                String commitId = commitNode.path("id").asText();
                String commitUrl = commitNode.path("url").asText();
                String repoName = rootNode.path("repository").path("name").asText();
                String repoUrl = rootNode.path("repository").path("url").asText();
                String avatarUrl = rootNode.path("sender").path("avatar_url").asText();

                EmbedCreateSpec embed = EmbedCreateSpec.builder()
                        .color(randomColor)
                        .title("Repository Update Notification")
                        .addField("Pusher", pusherName, false)
                        .thumbnail(avatarUrl)
                        .addField("Repository", repoName, false)
                        .addField("Repository URL: ", repoUrl, false)
                        .addField("Commit ID", commitId, false)
                        .addField("Commit URL", commitUrl, false)
                        .addField("Commit Author", authorName, false)
                        .addField("Commit Message", message, false)
                        .timestamp(Instant.now())
                        .build();

                for (DiscordBotInstance discordBotInstance : discordBotInstances) {
                    String channelId = discordBotInstance.getChannelId();
                    MessageChannel channel = (MessageChannel) client.getChannelById(Snowflake.of(channelId)).block();
                    if (channel != null) {
                        channel.createMessage(embed)
                                .onErrorResume(e -> {
                                    log.error("Failed to send message", e);
                                    return Mono.empty();
                                })
                                .block();
                    }
                }
            }
        } catch (IOException e) {
            log.error("An error occurred while processing the webhook: " + e.getMessage());
        }
    }

}
