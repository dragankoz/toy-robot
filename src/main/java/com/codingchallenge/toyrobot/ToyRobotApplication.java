package com.codingchallenge.toyrobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@SpringBootApplication
@EnableSwagger2
public class ToyRobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToyRobotApplication.class, args);
    }

    @Bean
    public Docket newApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Toy Robot")
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rest/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Toy Robot")
                .description("REST API for Toy Robot")
                .version("1.0.0")
                .build();
    }

}
