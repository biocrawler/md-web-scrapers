package org.perpetualnetworks.mdcrawlerconsumer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
//@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class})
@EntityScan(basePackages = {"org.perpetualnetworks.mdcrawlerconsumer"})
@SpringBootApplication
@EnableAsync
@EnableScheduling
//TODO: set server for prod
//servers = {@Server(url = "https://mdcrawler-api.perpetualnetworks.org/swagger")}
@Import({JacksonAutoConfiguration.class,
        PropertySourcesPlaceholderConfigurer.class,
        ThymeleafAutoConfiguration.class})
@OpenAPIDefinition(info = @Info(title = Constants.Swagger.TITLE,
        version = Constants.Swagger.VERSION,
        description = Constants.Swagger.DESCRIPTION,
        termsOfService = "bob",
        license = @License(name = "BSD", url = ""),
        contact = @Contact(name = "Contact developer", email = Constants.Swagger.EMAIL)))
//@ExternalDocumentation(url = "external doc url", description = "external doc desc")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
