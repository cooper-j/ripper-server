package com.hexan.ripper

import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.jdbc.datasource.init.DatabasePopulator
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore


@Configuration
class DataBaseConfig {
    @Value("\${spring.datasource.url}")
    private val datasourceUrl: String? = null

    @Value("\${spring.datasource.driverClassName}")
    private val dbDriverClassName: String? = null

    @Value("\${spring.datasource.username}")
    private val dbUsername: String? = null

    @Value("\${spring.datasource.password}")
    private val dbPassword: String? = null

    @Value("classpath:schema.sql")
    private val schemaScript: Resource? = null

    @Bean
    fun dataSourceInitializer(): DataSourceInitializer {
        val initializer = DataSourceInitializer()
        initializer.setDataSource(dataSource())
        initializer.setDatabasePopulator(databasePopulator())
        return initializer
    }

    private fun databasePopulator(): DatabasePopulator {
        val populator = ResourceDatabasePopulator()
        //populator.addScript(schemaScript!!)
        return populator
    }

    @Bean
    fun authorizationCodeServices(): AuthorizationCodeServices {
        return JdbcAuthorizationCodeServices(dataSource())
    }

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()

        dataSource.setDriverClassName(dbDriverClassName!!)
        dataSource.url = datasourceUrl
        dataSource.username = dbUsername!!
        dataSource.password = dbPassword!!

        return dataSource
    }

    @Bean
    fun tokenStore(): TokenStore {
        //return JdbcTokenStore(dataSource())
        return InMemoryTokenStore()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}