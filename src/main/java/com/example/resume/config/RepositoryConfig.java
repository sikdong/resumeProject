package com.example.resume.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.resume.*.repository")
@EnableElasticsearchRepositories(basePackages = "com.example.resume.*.search")
public class RepositoryConfig {
}
