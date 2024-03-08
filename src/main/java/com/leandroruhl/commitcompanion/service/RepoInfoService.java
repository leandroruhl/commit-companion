package com.leandroruhl.commitcompanion.service;

import com.leandroruhl.commitcompanion.repository.RepoInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RepoInfoService {
    private final RepoInfoRepository repoInfoRepository;

    public Boolean existsById(Long id) {
        return repoInfoRepository.existsById(id);
    }
}
