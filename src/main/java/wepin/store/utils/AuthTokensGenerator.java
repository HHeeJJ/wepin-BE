package wepin.store.utils;

import org.springframework.stereotype.Component;

import java.util.Date;

import lombok.RequiredArgsConstructor;
import wepin.store.dto.AuthTokenDto;
import wepin.store.enumeration.TokenType;
import wepin.store.jwt.JwtTokenProvider;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {
    private static final String BEARER_TYPE               = "Bearer";
    private static final long   ACCESS_TOKEN_EXPIRE_TIME  = 1000 * 60 * 30;            // 30분
    private static final long   REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final JwtTokenProvider jwtTokenProvider;

    public AuthTokenDto generate(String memberId, String token) {
        long now                   = (new Date()).getTime();
        Date accessTokenExpiredAt  = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String accessToken  = jwtTokenProvider.generate(memberId, TokenType.ACCESS.getName(), accessTokenExpiredAt, token);
        String refreshToken = jwtTokenProvider.generate(memberId, TokenType.REFRESH.getName(), refreshTokenExpiredAt, token);

        return AuthTokenDto.builder()
                           .accessToken(accessToken)
                           .refreshToken(refreshToken)
                           .grantType(BEARER_TYPE)
                           .expiresIn(ACCESS_TOKEN_EXPIRE_TIME / 1000L)
                           .build();
    }

}