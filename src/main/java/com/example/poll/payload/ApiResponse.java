package com.example.poll.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huhao
 * @created 2022/10/10
 * Description ApiResponse
 */
@Data
@AllArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
}
