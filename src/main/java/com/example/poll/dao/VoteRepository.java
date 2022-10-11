package com.example.poll.dao;

import com.example.poll.model.ChoiceVoteCount;
import com.example.poll.model.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author huhao
 * @created 2022/10/11
 * Description VoteRepository
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("select new com.example.poll.model.ChoiceVoteCount(v.choice.id,count(v.id)) from Vote v where v.poll.id in :pollIds group by v.choice.id")
    List<ChoiceVoteCount> countByPollIdInGroupByChoiceId(@Param("pollIds") List<Long> pollIds);

    @Query("select new com.example.poll.model.ChoiceVoteCount(v.choice.id,count(v.id)) from Vote v where v.poll.id = :pollId group by v.choice.id")
    List<ChoiceVoteCount> countByPollIdGroupByChoiceId(@Param("pollId") Long pollId);

    @Query("select v from Vote v where v.user.id=:userId and v.poll.id in :pollIds")
    List<Vote> findByUserIdAndPollIdIn(@Param("userId") Long userId, @Param("pollIds") List<Long> pollIds);

    @Query("SELECT v FROM Vote v where v.user.id = :userId and v.poll.id = :pollId")
    Vote findByUserIdAndPollId(@Param("userId") Long userId, @Param("pollId") Long pollId);

    @Query("SELECT COUNT(v.id) from Vote v where v.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT v.poll.id FROM Vote v WHERE v.user.id = :userId")
    Page<Long> findVotedPollIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}
