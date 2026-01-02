package io.github.haidarim.api.dto.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AuthenticationResponse {
    private String token;
}