package com.hexan.secustream

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(basePackages = ["com.hexan.secustream.secustream.repo"])
@EntityScan(basePackages = ["com.hexan.secustream.secustream"])
public class DataBaseConfig {
}