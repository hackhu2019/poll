package com.example.poll.service;

import com.example.poll.model.entity.Poll;
import com.example.poll.payload.PagedResponse;
import com.example.poll.payload.PollRequest;
import com.example.poll.payload.PollResponse;
import com.example.poll.payload.VoteRequest;
import com.example.poll.security.UserPrincipal;

/**
 * @author huhao
 * @create 2022/10/11
 */
public interface PollService {
    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size);

    public PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int page, int size);

    public PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size);

    public Poll createPoll(PollRequest pollRequest);

    public PollResponse getPollById(Long pollId, UserPrincipal currentUser);

    public PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser);
    }
