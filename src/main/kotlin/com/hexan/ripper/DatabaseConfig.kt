package com.hexan.ripper

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(basePackages = ["com.hexan.ripper.repo"])
@EntityScan(basePackages = ["com.hexan.ripper"])
public class DataBaseConfig {
}