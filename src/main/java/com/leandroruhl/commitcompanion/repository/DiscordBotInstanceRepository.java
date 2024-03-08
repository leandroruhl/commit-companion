package com.leandroruhl.commitcompanion.repository;

import com.leandroruhl.commitcompanion.model.DiscordBotInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscordBotInstanceRepository extends JpaRepository<DiscordBotInstance, Long> {
    List<DiscordBotInstance> findAllByRepositoryIdsContaining(Long repositoryId);
}
