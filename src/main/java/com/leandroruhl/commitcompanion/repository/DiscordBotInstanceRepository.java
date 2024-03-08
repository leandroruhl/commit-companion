package com.leandroruhl.commitcompanion.repository;

import com.leandroruhl.commitcompanion.model.DiscordBotInstance;
import com.leandroruhl.commitcompanion.model.RepoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscordBotInstanceRepository extends JpaRepository<DiscordBotInstance, Long> {
    List<DiscordBotInstance> findAllByRepositoriesContaining(RepoInfo repoInfo);
    Optional<DiscordBotInstance> getByServerId(String serverId);
}
