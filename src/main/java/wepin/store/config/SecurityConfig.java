package wepin.store.config;


import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.filter.CorsFilter;

import lombok.AllArgsConstructor;
import wepin.store.filter.JwtFilter;
import wepin.store.jwt.JwtTokenProvider;


@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // Spring Security Configuration Class를 작성하기 위해서는 WebSecurityConfigurerAdapter를 상속하여 클래스를 생성하고
    // @EnableWebSecurity 어노테이션을 추가해야 합니다(@Configuration 어노테이션 대신 사용).

    private final JwtTokenProvider jwtTokenProvider;
    private final CorsFilter       corsFilter;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
           .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**", "/css/**", "/js/**", "/images/**");
        web.httpFirewall(defaultHttpFirewall());
    }


    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtFilter jwtFilter = new JwtFilter(jwtTokenProvider);

        http.csrf()
            .disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()

            // 세션을 사용하지 않기 때문에 STATELESS로 설정
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .authorizeRequests()
            //            .antMatchers("/login", "/auth/**")
            .antMatchers("/**")
            .permitAll()
            .antMatchers("/swagger-ui/**")
            .permitAll()

            .anyRequest()
            .authenticated()

            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

}