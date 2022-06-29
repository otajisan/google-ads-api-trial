package io.morningcode.googleauthgateway.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

  @Bean
  fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    http.authorizeExchange()
        .anyExchange()
        .authenticated()
        .and()
        .oauth2Login(Customizer.withDefaults())
    http.csrf().disable()

    return http.build()
  }
}