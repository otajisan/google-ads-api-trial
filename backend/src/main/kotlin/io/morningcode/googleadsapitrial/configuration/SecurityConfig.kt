package io.morningcode.googleadsapitrial.configuration

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
class SecurityConfig {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

    http
        .authorizeRequests()
        //.antMatchers("/auth/google/").permitAll()
        .antMatchers("/ads/**").permitAll()
        .antMatchers("/auth/google/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .oauth2Login()

    return http.build()
  }

}