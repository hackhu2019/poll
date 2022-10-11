package com.example.poll.service;

import com.example.poll.dao.PollRepository;
import com.example.poll.dao.UserRepository;
import com.example.poll.dao.VoteRepository;
import com.example.poll.exception.BadRequestException;
import com.example.poll.exception.ResourceNotFoundException;
import com.example.poll.model.ChoiceVoteCount;
import com.example.poll.model.entity.Choice;
import com.example.poll.model.entity.Poll;
import com.example.poll.model.entity.User;
import com.example.poll.model.entity.Vote;
import com.example.poll.payload.PagedResponse;
import com.example.poll.payload.PollRequest;
import com.example.poll.payload.PollResponse;
import com.example.poll.payload.VoteRequest;
import com.example.poll.security.UserPrincipal;
import com.example.poll.util.AppConstants;
import com.example.poll.util.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huhao
 * @created 2022/10/11
 * Description PollService
 */
@Service
@Slf4j
public class PollServiceImpl implements PollService {
    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> polls = pollRepository.findAll(pageable);
        if (polls.getNumberOfElements() == 0) {
            return new PagedResponse<>(
                    Collections.emptyList(), polls.getNumber(), polls.getSize(), polls.getTotalElements()
                    , polls.getTotalPages(), polls.isLast());
        }
        List<Long> pollIds = polls.map(Poll::getId).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
        Map<Long, User> creatorMap = getPollCreatorMap(polls.getContent());
        List<PollResponse> pollResponses = polls.map(poll -> ModelMapper.mapPollToPollResponse(
                poll, choiceVoteCountMap, creatorMap.get(poll.getCreatedBy())
                , pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null)
        )).getContent();
        return new PagedResponse(pollResponses, polls.getNumber(), polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
    }

    @Override
    public PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> polls = pollRepository.findByCreatedBy(user.getId(), pageable);
        if (polls.getNumberOfElements() == 0) {
            return new PagedResponse<>(
                    Collections.emptyList(), polls.getNumber(), polls.getSize(), polls.getTotalElements()
                    , polls.getTotalPages(), polls.isLast());
        }
        List<Long> pollIds = polls.map(Poll::getId).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
        Map<Long, User> creatorMap = getPollCreatorMap(polls.getContent());
        List<PollResponse> pollResponses = polls.map(poll -> ModelMapper.mapPollToPollResponse(
                poll, choiceVoteCountMap, creatorMap.get(poll.getCreatedBy())
                , pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null)
        )).getContent();
        return new PagedResponse(pollResponses, polls.getNumber(), polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
    }

    @Override
    public PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        // Retrieve all pollIds in which the given username has voted
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Long> userVotedPollIds = voteRepository.findVotedPollIdsByUserId(user.getId(), pageable);

        if (userVotedPollIds.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), userVotedPollIds.getNumber(),
                    userVotedPollIds.getSize(), userVotedPollIds.getTotalElements(),
                    userVotedPollIds.getTotalPages(), userVotedPollIds.isLast());
        }

        // Retrieve all poll details from the voted pollIds.
        List<Long> pollIds = userVotedPollIds.getContent();

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Poll> polls = pollRepository.findByIdIn(pollIds, sort);

        // Map Polls to PollResponses containing vote counts and poll creator details
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
        Map<Long, User> creatorMap = getPollCreatorMap(polls);

        List<PollResponse> pollResponses = polls.stream().map(poll -> ModelMapper.mapPollToPollResponse(poll,
                choiceVoteCountMap,
                creatorMap.get(poll.getCreatedBy()),
                pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null))).collect(Collectors.toList());

        return new PagedResponse<>(pollResponses, userVotedPollIds.getNumber(), userVotedPollIds.getSize(), userVotedPollIds.getTotalElements(), userVotedPollIds.getTotalPages(), userVotedPollIds.isLast());
    }

    @Override
    public Poll createPoll(PollRequest pollRequest) {
        Poll poll = new Poll();
        poll.setQuestion(pollRequest.getQuestion());
        List<Choice> choiceList = pollRequest.getChoices()
                .stream().map(choiceRequest -> new Choice(choiceRequest.getText())).collect(Collectors.toList());
        poll.setChoices(choiceList);
        Instant now = Instant.now();
        Instant expirationDateTime = now.plus(Duration.ofDays(pollRequest.getPollLength().getDays()))
                .plus(Duration.ofHours(pollRequest.getPollLength().getHours()));

        poll.setExpirationDateTime(expirationDateTime);

        return pollRepository.save(poll);
    }

    @Override
    public PollResponse getPollById(Long pollId, UserPrincipal currentUser) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ResourceNotFoundException("Poll", "id", pollId));
        List<ChoiceVoteCount> choiceVoteCounts = voteRepository.countByPollIdGroupByChoiceId(pollId);
        Map<Long, Long> choiceVoteMap = choiceVoteCounts.stream().collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
        User creator = userRepository.findById(poll.getCreatedBy()).orElseThrow(() -> new ResourceNotFoundException("User", "id", poll.getCreatedBy()));
        Vote vote = null;
        if (currentUser != null) {
            vote = voteRepository.findByUserIdAndPollId(currentUser.getId(), pollId);
        }
        return ModelMapper.mapPollToPollResponse(poll, choiceVoteMap, creator, vote != null ? vote.getChoice().getId() : null);
    }

    @Override
    public PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll", "id", pollId));

        if (poll.getExpirationDateTime().isBefore(Instant.now())) {
            throw new BadRequestException("Sorry! This Poll has already expired");
        }

        User user = userRepository.getById(currentUser.getId());

        Choice selectedChoice = poll.getChoices().stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Choices", "id", voteRequest.getChoiceId()));
        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setUser(user);
        vote.setChoice(selectedChoice);

        try {
            vote = voteRepository.save(vote);
        } catch (DataIntegrityViolationException ex) {
            log.info("User {} has already voted in Poll {}", currentUser.getId(), pollId);
            throw new BadRequestException("Sorry! You have already cast your vote in this poll");
        }
        List<ChoiceVoteCount> choiceVoteCounts = voteRepository.countByPollIdGroupByChoiceId(pollId);
        Map<Long, Long> choiceMap = choiceVoteCounts.stream().collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
        User creator = userRepository.findById(poll.getCreatedBy()).orElseThrow(() -> new ResourceNotFoundException("User", "id", poll.getCreatedBy()));


        return ModelMapper.mapPollToPollResponse(poll, choiceMap, creator, vote.getChoice().getId());
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero");
        }
        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds) {
        List<ChoiceVoteCount> choiceVoteCounts = voteRepository.countByPollIdInGroupByChoiceId(pollIds);
        Map<Long, Long> choiceVoteMap = choiceVoteCounts.stream().collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
        return choiceVoteMap;
    }

    private Map<Long, Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds) {
        Map<Long, Long> pollUserVoteMap = null;
        if (currentUser != null) {
            List<Vote> userVotes = voteRepository.findByUserIdAndPollIdIn(currentUser.getId(), pollIds);
            pollUserVoteMap = userVotes.stream().collect(Collectors.toMap(vote -> vote.getPoll().getId(), vote -> vote.getChoice().getId()));
        }
        return pollUserVoteMap;
    }

    Map<Long, User> getPollCreatorMap(List<Poll> polls) {
        List<Long> creatorIds = polls.stream().map(Poll::getCreatedBy).distinct().collect(Collectors.toList());
        List<User> creators = userRepository.findByIdIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        return creatorMap;
    }
}
