package com.leandroruhl.commitcompanion.service;

import com.leandroruhl.commitcompanion.model.DiscordBotInstance;
import com.leandroruhl.commitcompanion.repository.DiscordBotInstanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiscordBotInstanceService {
    private final DiscordBotInstanceRepository discordBotInstanceRepository;

    public List<DiscordBotInstance> getBotInstancesByRepositoryId(Long repositoryId) {
        return discordBotInstanceRepository.findAllByRepositoryIdsContaining(repositoryId);
    }
}
