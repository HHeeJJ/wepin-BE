package wepin.store.config;


import com.fasterxml.classmate.TypeResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final TypeResolver typeResolver;

    @Autowired
    public SwaggerConfig(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).alternateTypeRules(
                                                              AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class), typeResolver.resolve(Page.class)))
                                                      .useDefaultResponseMessages(true)
                                                      .apiInfo(apiInfo())
                                                      .select()
                                                      .apis(RequestHandlerSelectors.basePackage("wepin.store.controller"))
                                                      .paths(PathSelectors.any())
                                                      .build()
                                                      .securitySchemes(Arrays.asList(apiKey()));
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Wepin Rest API Documentation")
                                   .description("Wepin Swagger")
                                   .version("0.1")
                                   .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("Token", "Authorization", "header");
    }


    @Data
    @ApiModel
    static class Page {
        @ApiModelProperty(value = "페이지 번호(0..N)")
        private Integer page;

        @ApiModelProperty(value = "페이지 크기", allowableValues = "range[0, 100]")
        private Integer size;

        @ApiModelProperty(value = "정렬(사용법: 컬럼명,ASC|DESC)")
        private List<String> sort;
    }
}