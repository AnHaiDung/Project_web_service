package com.demo.repository;

import com.demo.model.entity.Application;
import com.demo.model.entity.Job;
import com.demo.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByJobAndCandidate(Job job, User candidate);

    Page<Application> findByCandidate(User candidate, Pageable pageable);

    Page<Application> findByJobEmployer(User employer, Pageable pageable);
}
