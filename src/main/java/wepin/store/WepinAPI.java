package wepin.store;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableScheduling
@SpringBootApplication
@MapperScan("wepin.store.mapper")
public class WepinAPI {
    @Value("${web-url}")
    private String WEB_URL;

    public static void main(String[] args) {
        SpringApplication.run(WepinAPI.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(WEB_URL);
            }

            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                WebMvcConfigurer.super.configureMessageConverters(converters);
                converters.add(new FormHttpMessageConverter());
            }
        };
    }
}