package com.example.poll.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huhao
 * @created 2022/10/11
 * Description UserSummary
 */
@Data
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String username;
    private String name;
}
