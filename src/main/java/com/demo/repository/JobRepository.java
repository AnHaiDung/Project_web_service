package com.demo.repository;

import com.demo.model.entity.Job;
import com.demo.model.entity.JobStatus;
import com.demo.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    Page<Job> findByEmployer(User employer, Pageable pageable);

    Page<Job> findByStatusAndActiveTrueAndTitleContainingIgnoreCase(
            JobStatus status,
            String keyword,
            Pageable pageable
    );
}
