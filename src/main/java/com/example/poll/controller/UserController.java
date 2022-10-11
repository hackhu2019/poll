package com.example.poll.controller;

import com.example.poll.dao.PollRepository;
import com.example.poll.dao.UserRepository;
import com.example.poll.dao.VoteRepository;
import com.example.poll.exception.ResourceNotFoundException;
import com.example.poll.model.entity.User;
import com.example.poll.payload.*;
import com.example.poll.security.CurrentUser;
import com.example.poll.security.UserPrincipal;
import com.example.poll.service.PollService;
import com.example.poll.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author huhao
 * @created 2022/10/11
 * Description UserController
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollService pollService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
        return userSummary;
    }

    @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam("username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam("email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/{username}")
    public UserProfile getUserProfile(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        long pollCnt = pollRepository.countByCreatedBy(user.getId());
        long voteCnt = voteRepository.countByUserId(user.getId());
        UserProfile userProfile = new UserProfile(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCnt, voteCnt);
        return userProfile;
    }

    @GetMapping("/{username}/polls")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username
            , @CurrentUser UserPrincipal currentUser
            , @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page
            , @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return pollService.getPollsCreatedBy(username, currentUser, page, size);
    }

    @GetMapping("/{username}/votes")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username
            , @CurrentUser UserPrincipal currentUser
            , @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page
            , @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return pollService.getPollsVotedBy(username, currentUser, page, size);
    }
}
