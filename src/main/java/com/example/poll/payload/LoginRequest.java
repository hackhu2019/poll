package com.example.poll.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author huhao
 * @created 2022/10/10
 * Description LoginRequest
 */
@Data
public class LoginRequest {
    @NotBlank
    private String usernameOrEmail;
    @NotBlank
    private String password;
}
