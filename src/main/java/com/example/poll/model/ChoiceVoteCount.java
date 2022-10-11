package com.example.poll.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huhao
 * @created 2022/10/11
 * Description ChoiceVoteCount
 */
@Data
@AllArgsConstructor
public class ChoiceVoteCount {
    private Long choiceId;
    private Long voteCount;
}
