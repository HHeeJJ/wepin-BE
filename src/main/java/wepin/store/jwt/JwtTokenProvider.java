package wepin.store.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wepin.store.enumeration.TokenType;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generate(String memberId, String type, Date expiredAt, String token) {

        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("accessToken", token);

        return Jwts.builder()
                .setHeaderParam(TokenType.TYPE.getName(), type)
                .setClaims(claims)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }


    // 회원 정보 조회
    public String getMemberId(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .get("memberId", String.class);
    }

    public String getAccessToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .get("accessToken", String.class);
    }

    // 토큰 유효 및 만료 확인
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }


    // refresh 토큰 확인
    public boolean isRefreshToken(String token) {

        Header header = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getHeader();

        if (header.get(TokenType.TYPE.getName()).toString().equals(TokenType.REFRESH.getName())) {
            return true;
        }
        return false;
    }

    // access 토큰 확인
    public boolean isAccessToken(String token) {

        Header header = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getHeader();

        if (header.get(TokenType.TYPE.getName()).toString().equals(TokenType.ACCESS.getName())) {
            return true;
        }
        return false;
    }

    public String resolveToken(HttpServletRequest request) {
        // HTTP 요청 헤더에서 "Authorization" 헤더 값을 가져옵니다.
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 부분을 제외한 토큰 부분을 추출합니다.
            return bearerToken.substring(7);
        }

        return null;
    }


}