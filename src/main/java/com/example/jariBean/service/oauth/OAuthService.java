package com.example.jariBean.service.oauth;

import com.example.jariBean.config.jwt.JwtProcess;
import com.example.jariBean.dto.oauth.LoginResDto.LoginSuccessResDto;
import com.example.jariBean.entity.Token;
import com.example.jariBean.entity.User;
import com.example.jariBean.handler.ex.CustomApiException;
import com.example.jariBean.repository.TokenRepository;
import com.example.jariBean.repository.user.UserRepository;
import com.example.jariBean.service.oauth.OAuthKakaoService.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.jariBean.entity.User.UserRole.UNREGISTERED;

@Service
@RequiredArgsConstructor
public abstract class OAuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtProcess jwtProcess;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public abstract String getAccessToken(String code);

    public abstract SocialUserInfo getUserInfo(String accessToken);

    public LoginSuccessResDto saveOrUpdate(SocialUserInfo socialUserInfo, String registrationId) {

        // save or create user
        User user = userRepository.findBySocialId(socialUserInfo.getSocialId())
                .orElse(User.builder()
                        .socialId(socialUserInfo.getSocialId())
                        .password(passwordEncoder.encode(socialUserInfo.getNickname()))
                        .role(UNREGISTERED)
                        .build());

        // update user info
        user.updateBySocialInfo(socialUserInfo.getNickname(), socialUserInfo.getImageUrl(), passwordEncoder.encode(socialUserInfo.getNickname()));

        // save or update user
        User savedUser = userRepository.save(user);

        //create JWT
        String accessToken = jwtProcess.createAccessToken(savedUser);
        String refreshToken = jwtProcess.createRefreshToken(savedUser);

        // storing jwt in redis
        Token token = Token.builder()
                .userId(savedUser.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // exception for connect redis
        try {
            tokenRepository.save(token);
        } catch (Exception e) {
            throw new CustomApiException("JWT를 REDIS에 저장하는 과정에서 오류가 발생했습니다.");
        }

        return LoginSuccessResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public boolean isExistUser(String socialId) {
        return userRepository.existsBySocialId(socialId);
    }
}