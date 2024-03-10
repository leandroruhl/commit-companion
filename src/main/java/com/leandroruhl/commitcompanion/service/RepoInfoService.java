package com.leandroruhl.commitcompanion.service;

import com.leandroruhl.commitcompanion.model.RepoInfo;
import com.leandroruhl.commitcompanion.repository.RepoInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RepoInfoService {
    private final RepoInfoRepository repoInfoRepository;

    public Optional<RepoInfo> findById(Long id) {
        return repoInfoRepository.findById(id);
    }

    public Boolean existsById(Long id) {
        return repoInfoRepository.existsById(id);
    }
}
