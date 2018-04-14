package com.hexan.ripper

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.provider.token.TokenStore




@Configuration
@EnableResourceServer
class ResourceServerConfig : ResourceServerConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity)  {
        //-- define URL patterns to enable OAuth2 security
        //http!!.anonymous().disable()
          //      .requestMatchers().antMatchers("/api/**")
            //    .and().authorizeRequests()
              //  .antMatchers("/api/**").access("hasRole('ADMIN') or hasRole('USER')")
                //.and().exceptionHandling().accessDeniedHandler(OAuth2AccessDeniedHandler())


        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/signup").permitAll()
                .antMatchers("/oauth/token").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .realmName("REALM")
    }
}
