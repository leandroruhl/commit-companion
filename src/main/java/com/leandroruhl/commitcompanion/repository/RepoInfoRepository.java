package com.leandroruhl.commitcompanion.repository;

import com.leandroruhl.commitcompanion.model.RepoInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoInfoRepository extends JpaRepository<RepoInfo, Long> {
}
