package com.example.poll.dao;

import com.example.poll.model.entity.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author huhao
 * @created 2022/10/11
 * Description PollRepository
 */
@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findById(Long id);

    Page<Poll> findByCreatedBy(Long userId, Pageable pageable);

    long countByCreatedBy(Long userId);

    List<Poll> findByIdIn(List<Long> pollIds);

    List<Poll> findByIdIn(List<Long> pollIds, Sort sort);
}
