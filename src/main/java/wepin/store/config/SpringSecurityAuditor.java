package wepin.store.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringSecurityAuditor implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();  // 인증된 사용자가 없을 경우
        }

        return Optional.of(authentication.getName());  // 현재 인증된 사용자 반환
    }
}