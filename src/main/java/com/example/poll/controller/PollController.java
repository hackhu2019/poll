package com.example.poll.controller;

import com.example.poll.dao.PollRepository;
import com.example.poll.dao.UserRepository;
import com.example.poll.dao.VoteRepository;
import com.example.poll.model.entity.Poll;
import com.example.poll.payload.*;
import com.example.poll.security.CurrentUser;
import com.example.poll.security.UserPrincipal;
import com.example.poll.service.PollService;
import com.example.poll.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

/**
 * @author huhao
 * @created 2022/10/11
 * Description pollController
 */
@RestController
@RequestMapping("/api/poll")
@Slf4j
public class PollController {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollService pollService;

    @GetMapping
    public PagedResponse<PollResponse> getPolls(@CurrentUser UserPrincipal currentUser,
                                                @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getAllPolls(currentUser, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest) {
        Poll poll = pollService.createPoll(pollRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pollId}")
                .buildAndExpand(poll.getId()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "Poll Created Successfully"));
    }

    @GetMapping("/{pollId}")
    public PollResponse getPollById(@CurrentUser UserPrincipal currentUser, @PathVariable(value = "pollId") Long pollId) {
        return pollService.getPollById(pollId, currentUser);
    }

    @PostMapping("/{pollId}/votes")
    @PreAuthorize("hasRole('USER')")
    public PollResponse castVote(@CurrentUser UserPrincipal currentUser
            , @PathVariable(value = "pollId") Long pollId
            , @Valid @RequestBody VoteRequest voteRequest) {
        return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, currentUser);
    }
}
