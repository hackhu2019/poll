package com.example.poll.util;

import com.example.poll.model.entity.Poll;
import com.example.poll.model.entity.User;
import com.example.poll.payload.ChoiceResponse;
import com.example.poll.payload.PollResponse;
import com.example.poll.payload.UserSummary;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huhao
 * @created 2022/10/11
 * Description ModelMapper
 */
public class ModelMapper {
    public static PollResponse mapPollToPollResponse(Poll poll, Map<Long, Long> choiceVotesMap, User creator, Long userVote) {
        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(poll.getId());
        pollResponse.setQuestion(poll.getQuestion());
        pollResponse.setCreationDateTime(poll.getCreatedAt());
        pollResponse.setExpirationDateTime(poll.getExpirationDateTime());
        Instant now = Instant.now();
        pollResponse.setIsExpired(poll.getExpirationDateTime().isBefore(now));
        List<ChoiceResponse> choiceResponseList = poll.getChoices()
                .stream().map(choice -> {
                    ChoiceResponse choiceResponse = new ChoiceResponse();
                    choiceResponse.setId(choice.getId());
                    choiceResponse.setText(choice.getText());
                    choiceResponse.setVoteCount(choiceVotesMap.containsKey(choice.getId()) ? choiceVotesMap.get(choice.getId()) : 0);
                    return choiceResponse;
                }).collect(Collectors.toList());
        pollResponse.setChoices(choiceResponseList);
        UserSummary userSummary = new UserSummary(creator.getId(), creator.getUsername(), creator.getName());
        pollResponse.setCreatedBy(userSummary);
        if (userVote != null) {
            pollResponse.setSelectedChoice(userVote);
        }
        long totalVotes = pollResponse.getChoices().stream().mapToLong(ChoiceResponse::getVoteCount).sum();
        pollResponse.setTotalVotes(totalVotes);
        return pollResponse;
    }
}
