package com.example.jariBean.entity;

import com.example.jariBean.config.jwt.JwtVO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Getter
@Setter
@RedisHash(timeToLive = JwtVO.REFRESH_EXPIRATION_TIME)
public class Token {

    @Id
    private String phoneNumber;
    private String refreshToken;
    private String firebaseToken;


    @Builder
    public Token(String phoneNumber, String refreshToken, String firebaseToken) {
        this.phoneNumber = phoneNumber;
        this.refreshToken = refreshToken;
        this.firebaseToken = firebaseToken;
    }

}