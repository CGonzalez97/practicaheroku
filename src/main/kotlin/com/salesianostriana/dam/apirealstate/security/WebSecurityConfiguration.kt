package com.salesianostriana.dam.apirealstate.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.http.HttpMethod.*




@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfiguration(private val userDetailsService: UserDetailsService,
                               private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
                               private val jwtAuthorizationFilter: JwtAuthorizationFilter,
                               private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter(){

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/h2-console").permitAll()
                .antMatchers(POST, "/auth/login","/auth/token","/user","/auth/register").permitAll()
                .antMatchers(GET, "/viviendas", "/viviendas?cat=**&ciu=**&pre=**","/viviendas/{id}").permitAll()
                .antMatchers(POST,"/viviendas","/vivienda/{idV}/img").hasRole("USER")
                .antMatchers(GET, "/viviendas/mine","/viviendas/favs").hasRole("USER")
                .antMatchers(PUT, "/viviendas/{id}").hasRole("USER")
                .antMatchers(DELETE, "/viviendas/{id}","vivienda/{id}/img/{hash}").hasRole("USER")

        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.headers().frameOptions().disable()

    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}

@Configuration
class ConfigureCors(){

    @Bean
    fun corsConfigurer() = object: WebMvcConfigurer{

        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .maxAge(3600)
        }
    }
}
