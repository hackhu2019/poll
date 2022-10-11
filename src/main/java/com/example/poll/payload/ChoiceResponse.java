package com.example.poll.payload;

import lombok.Data;

/**
 * @author huhao
 * @created 2022/10/11
 * Description ChoiceResponse
 */
@Data
public class ChoiceResponse {
    private long id;
    private String text;
    private long voteCount;
}
