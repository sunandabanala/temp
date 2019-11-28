package com.auzmor.calendar.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

import static com.auzmor.calendar.constants.SwaggerConstant.*;


@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .globalOperationParameters(
        Arrays.asList(new ParameterBuilder()
          .name("Authorization")
          .description("Application's id")
          .modelRef(new ModelRef("string"))
          .parameterType("header")
          .required(false).defaultValue("Bearer")
          .build()
        )
      )
      .genericModelSubstitutes(ResponseEntity.class)
      .select()
      .apis(RequestHandlerSelectors.any())
      .paths(PathSelectors.any())
      .build()
      .apiInfo(apiInfo()).useDefaultResponseMessages(false);
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
      TITLE,
      DESCRIPTION,
      VERSION,
      TERMS_OF_SERVICE_URL,
      new Contact(DEVELOPER_NAME, "url", DEVELOPER_EMAIL),
      LICENCE_URL, LICENCE_URL, Collections.emptyList());
  }
}


