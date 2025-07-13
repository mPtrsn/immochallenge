package com.malte.immochallenge.spotify.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccessTokenResponse {
    String access_token;
    String token_type;
    long expires_in;
}