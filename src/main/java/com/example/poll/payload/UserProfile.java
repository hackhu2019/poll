package com.example.poll.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * @author huhao
 * @created 2022/10/11
 * Description UserProfile
 */
@Data
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private String username;
    private String name;
    private Instant joinedAt;
    private Long pollCount;
    private Long voteCount;
}
