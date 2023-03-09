package com.kt.myrestapi.config;

import com.kt.myrestapi.accounts.AccountService;
import com.kt.myrestapi.filter.CustomAuthenticationFilter;
import com.kt.myrestapi.filter.CustomAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    AccountService accountService;

    @Autowired
    Environment env;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));

        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        http.addFilter(customAuthenticationFilter(authenticationManager));
        http.authorizeHttpRequests().antMatchers(HttpMethod.PUT, "/api/lectures/**").permitAll()
//        http.authorizeHttpRequests()   // CustomAuthorizationFilter는 jwt를 해석해서 넘기는 역할을 하는 것이지 permitAll에 대한 판단을 하는 것이 아니다.
                .anyRequest().authenticated()
                .and()
                .addFilter(customAuthenticationFilter(authenticationManager))
                .addFilterBefore(customAuthorizationFilter(), CustomAuthenticationFilter.class);


        http.headers().frameOptions().disable();
        return http.build();
    }

    private CustomAuthorizationFilter customAuthorizationFilter() {
        return new CustomAuthorizationFilter(accountService, env);
    }

    private CustomAuthenticationFilter customAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(authenticationManager, accountService, env);
        authenticationFilter.setFilterProcessesUrl("/api/login");
        return authenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
