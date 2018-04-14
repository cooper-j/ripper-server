package com.hexan.ripper

import com.hexan.ripper.service.UserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler
import org.springframework.security.oauth2.provider.token.TokenStore


@Configuration
@EnableAuthorizationServer
class AuthServerOAuth2Config : AuthorizationServerConfigurerAdapter() {
    private val ONE_DAY = 60 * 60 * 24
    private val THIRTY_DAYS = 60 * 60 * 24 * 30

    @Autowired
    lateinit var tokenStore: TokenStore

    @Autowired
    lateinit var userApprovalHandler: UserApprovalHandler

    @Autowired
    @Qualifier("authenticationManagerBean")
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userDetailsService: UserDetailsService


    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer?) {
        clients!!.inMemory()
                .withClient("49l0tm6ar74tbfq72r5y")
                .secret("{noop}002hal5b8qodc0e36gzb")
                .authorizedGrantTypes("password", "refresh_token")
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                .scopes("read", "write", "trust")
                .accessTokenValiditySeconds(ONE_DAY)
                .refreshTokenValiditySeconds(THIRTY_DAYS)
    }

    @Throws(Exception::class)
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer?) {
        endpoints!!.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
    }

    @Throws(Exception::class)
    override fun configure(oauthServer: AuthorizationServerSecurityConfigurer?) {
        oauthServer!!.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
    }
}