package com.leandroruhl.commitcompanion.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leandroruhl.commitcompanion.model.DiscordBotInstance;
import com.leandroruhl.commitcompanion.model.RepoInfo;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DiscordService {
    private final GatewayDiscordClient client;
    private final DiscordBotInstanceService botInstanceService;
    private final RepoInfoService repoInfoService;

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

            for (JsonNode commitNode : commitsNode) {
                String message = commitNode.path("message").asText();
                String authorName = commitNode.path("author").path("name").asText();
                String commitId = commitNode.path("id").asText();
                String commitUrl = commitNode.path("url").asText();
                String repoName = rootNode.path("repository").path("name").asText();
                String repoUrl = rootNode.path("repository").path("url").asText();

                String discordMessage = String.format("**Pusher:** %s\n**Repository:** [%s](%s)\n**Commit:** [%s](%s)\n**Author:** %s\n```%s```",
                        pusherName, repoName, repoUrl, commitId, commitUrl, authorName, message);


                for (DiscordBotInstance discordBotInstance : discordBotInstances) {
                    String channelId = discordBotInstance.getChannelId();
                    MessageChannel channel = (MessageChannel) client.getChannelById(Snowflake.of(channelId)).block();
                    if (channel != null) {
                        channel.createMessage(discordMessage)
                                .onErrorResume(e -> {
                                    log.error("Failed to send message", e);
                                    return Mono.empty();
                                })
                                .subscribe();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
