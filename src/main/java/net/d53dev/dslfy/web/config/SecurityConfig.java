package net.d53dev.dslfy.web.config;

import net.d53dev.dslfy.web.auth.APIAuthenticationEntryPoint;
import net.d53dev.dslfy.web.auth.EHCacheSecurityContextRepository;
import net.d53dev.dslfy.web.client.ClientApiV1;
import net.d53dev.dslfy.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

/**
 * Created by davidsere on 16/11/15.
 */

@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    APIAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private EHCacheSecurityContextRepository securityContextRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .securityContext().securityContextRepository(securityContextRepository)
                .and()
                .authorizeRequests()
                .antMatchers(ClientApiV1.LOGIN).permitAll()
//                .antMatchers(ClientApiV1.apiPrefix+"**").hasAnyRole("USER")
                .antMatchers("/").permitAll()
                .anyRequest().authenticated();

//        http
//                .formLogin()
//
//                .loginPage("/login")
//                .permitAll()
//                .and()
//                .logout()
//                .permitAll();

        http.headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                        //.addHeaderWriter(new XContentTypeOptionsHeaderWriter())
                .addHeaderWriter(new XXssProtectionHeaderWriter())
                .addHeaderWriter(new CacheControlHeadersWriter())
                .addHeaderWriter(new HstsHeaderWriter());

        http
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .csrf()
                .disable();
    }
}
