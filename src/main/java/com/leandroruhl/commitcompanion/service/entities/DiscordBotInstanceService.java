package com.leandroruhl.commitcompanion.service.entities;

import com.leandroruhl.commitcompanion.model.DiscordBotInstance;
import com.leandroruhl.commitcompanion.model.RepoInfo;
import com.leandroruhl.commitcompanion.repository.DiscordBotInstanceRepository;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DiscordBotInstanceService {
    private final DiscordBotInstanceRepository discordBotInstanceRepository;
    private final RepoInfoService repoInfoService;
    private final RestTemplate restTemplate;

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

        String url = "https://api.github.com/repos/" + owner + "/" + repo;
        ResponseEntity<RepoInfo> responseEntity;

        try {
            responseEntity = restTemplate.getForEntity(url, RepoInfo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Repository not found");
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Client error occurred");
        } catch (HttpServerErrorException e) {
            throw new IllegalArgumentException("Server error occurred");
        }

        RepoInfo repository = responseEntity.getBody();

        if (repoInfoService.existsById(repository.getId())) {
            throw new IllegalArgumentException("Repository is already being watched");
        }

        List<RepoInfo> repositories = discordBotInstance.getRepositories();
        repositories.add(repository);
        discordBotInstance.setRepositories(repositories);
        discordBotInstanceRepository.save(discordBotInstance);
    }

    public EmbedCreateSpec createEmbed(List<RepoInfo> repoList) {
        EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder()
                .title("Repository Watch List")
                .description("List of GitHub repositories being watched by the bot")
                .color(Color.WHITE);

        for (RepoInfo repoInfo : repoList) {
            embed.addField("", "\u200B", false)
                    .addField("Name", repoInfo.getFull_name(), false)
                    .addField("ID", String.valueOf(repoInfo.getId()), false);
        }

        return embed.build();
    }

    public void removeRepositoryFromBotInstance(DiscordBotInstance discordBotInstance, String repoId) {
        Long id = Long.parseLong(repoId);

        if (!repoInfoService.existsById(id)) {
            throw new IllegalArgumentException("Repository is not being watched");
        }

        List<RepoInfo> repositories = discordBotInstance.getRepositories();
        repositories.removeIf(repo -> repo.getId().equals(id));
        discordBotInstanceRepository.save(discordBotInstance);
    }
}
