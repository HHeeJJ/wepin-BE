package wepin.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 크레덴셜(인증 정보)을 허용하고, 모든 도메인을 허용
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");

        // 허용할 HTTP 메서드와 헤더
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);

    }
}
