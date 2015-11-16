package net.d53dev.dslfy.web;

import net.d53dev.dslfy.web.repository.ImageRepository;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;

@EnableJpaRepositories(basePackageClasses = {UserRepository.class, ImageRepository.class})

@SpringBootApplication
public class DSLFYWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DSLFYWebApplication.class, args);
    }
}
