package com.leandroruhl.commitcompanion.service;

import com.leandroruhl.commitcompanion.model.DiscordBotInstance;
import com.leandroruhl.commitcompanion.model.RepoInfo;
import com.leandroruhl.commitcompanion.repository.DiscordBotInstanceRepository;
import com.leandroruhl.commitcompanion.repository.RepoInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DiscordBotInstanceService {
    private final DiscordBotInstanceRepository discordBotInstanceRepository;
    private final RepoInfoService repoInfoService;

    public List<DiscordBotInstance> getBotInstancesByRepository(RepoInfo repository) {
        return discordBotInstanceRepository.findAllByRepositoriesContaining(repository);
    }

    public DiscordBotInstance getBotInstanceByServerId(String serverId) {
        return discordBotInstanceRepository.getByServerId(serverId).orElse(null);
    }

    public void createBotInstance(String serverId, String channelId) {
        DiscordBotInstance discordBotInstance = new DiscordBotInstance(serverId, channelId);
        discordBotInstanceRepository.save(discordBotInstance);
    }

    public void changeBotInstanceChannel(DiscordBotInstance discordBotInstance, String newChannelId) {
        discordBotInstance.setChannelId(newChannelId);
        discordBotInstanceRepository.save(discordBotInstance);
    }

    public void addRepositoryToBotInstance(DiscordBotInstance discordBotInstance, String repoUrl) {
        // Extract owner and repo from the URL
        String[] parts = repoUrl.split("/");
        String owner = parts[parts.length - 2];
        String repo = parts[parts.length - 1];

        WebClient webClient = WebClient.create();

        RepoInfo repository = webClient.get()
                .uri("https://api.github.com/repos/" + owner + "/" + repo)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new IllegalArgumentException("Repository not found"));
                    }
                    return Mono.error(new IllegalArgumentException("Client error occurred"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new IllegalArgumentException("Server error occurred")))
                .bodyToMono(RepoInfo.class)
                .block();

        if (repoInfoService.existsById(repository.getId())) {
            throw new IllegalArgumentException("Repository is already being watched");
        }

        List<RepoInfo> repositories = discordBotInstance.getRepositories();
        repositories.add(repository);
        discordBotInstance.setRepositories(repositories);
        discordBotInstanceRepository.save(discordBotInstance);
    }
}
