package com.project.tour.repository;

import com.project.tour.domain.EstimateReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateReplyRepository extends JpaRepository<EstimateReply, Long> {
}
