package com.example.poll.payload;

import lombok.Data;

/**
 * @author huhao
 * @created 2022/10/10
 * Description JwtAuthenticationResponse
 */
@Data
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
