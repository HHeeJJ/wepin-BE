package wepin.store.service;


import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import wepin.store.dto.AuthTokenDto;
import wepin.store.jwt.JwtTokenProvider;
import wepin.store.utils.AuthTokensGenerator;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider    jwtTokenProvider;
    private final AuthTokensGenerator authTokensGenerator;

    public AuthTokenDto tokenReIssue(String refreshToken) throws Exception {
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new Exception("Refresh 토큰 유효성 검사 실패");
        } else {
            String kakaoToken = jwtTokenProvider.getAccessToken(refreshToken);
            String memberId   = jwtTokenProvider.getMemberId(refreshToken);

            return authTokensGenerator.generate(memberId, kakaoToken); // 재발급된 토큰 반환
        }
    }
}

