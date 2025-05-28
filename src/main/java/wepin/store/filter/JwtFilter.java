package wepin.store.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wepin.store.jwt.JwtTokenProvider;
import wepin.store.utils.ErrorUtill;

@Slf4j
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!"/".equals(request.getRequestURI())) {
            log.info("[RequestURI]: {}", request.getRequestURI());
        }

        // "/auth" 경로에 대한 요청인 경우 필터 체인을 종료하고 다음 필터로 넘어가지 않음
        if (request.getRequestURI()
                   .startsWith("/auth") || request.getRequestURI()
                                                  .startsWith("/log")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenProvider.resolveToken(request); // Bearer로 시작하는 값에서 Bearer를 제거한 accessToken(appToken) 반환
        if (token != null) {
            log.info("[token] :{}", token);
            if (!jwtTokenProvider.validateToken(token)) { // token이 유효한지 확인
                ErrorUtill.sendUnauthorizedError(response, "유효하지 않은 토큰입니다.");
                return; // 필터 체인 종료
            } else {
                log.info("[memberId]: " + jwtTokenProvider.getMemberId(token));
            }
        }

        filterChain.doFilter(request, response);
    }


}
