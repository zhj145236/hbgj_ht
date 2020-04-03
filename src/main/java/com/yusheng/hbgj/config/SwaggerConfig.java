package com.yusheng.hbgj.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger文档
 *
 * @author Jinwei
 * <p>
 * 2017年7月21日
 */
@Configuration
@EnableSwagger2
@Profile({"test","dev"})
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        ParameterBuilder builder = new ParameterBuilder();
        builder.parameterType("header").name("login-token")
                .description("restful方式的header参数")
                .required(false)
                .modelRef(new ModelRef("string")); // 在swagger里显示header

        return new Docket(DocumentationType.SWAGGER_2).groupName("swagger接口文档")
                .apiInfo(new ApiInfoBuilder().title("环保管家")
                        .contact(new Contact("昱升", "", "xxx@qq.com")).version("1.0").build())
                .globalOperationParameters(Lists.newArrayList(builder.build()))
                .select().paths(PathSelectors.any()).build();
    }
}
