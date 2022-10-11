package com.example.poll.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author huhao
 * @created 2022/10/11
 * Description
 */
@Data
public class VoteRequest {
    @NotNull
    private Long choiceId;
}
