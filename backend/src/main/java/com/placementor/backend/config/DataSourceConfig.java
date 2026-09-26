package com.placementor.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:}")
    private String springDatasourceUrl;

    @Value("${spring.datasource.username:}")
    private String springDatasourceUsername;

    @Value("${spring.datasource.password:}")
    private String springDatasourcePassword;

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        String url = springDatasourceUrl;
        String username = springDatasourceUsername;
        String password = springDatasourcePassword;

        if ((url == null || url.isBlank() || url.contains("localhost")) && databaseUrl != null && !databaseUrl.isBlank()) {
            log.info("Detected DATABASE_URL environment variable. Parsing connection parameters...");
            try {
                String cleanUrl = databaseUrl.trim();
                if (cleanUrl.startsWith("postgres://")) {
                    cleanUrl = "postgresql://" + cleanUrl.substring(11);
                }
                
                URI dbUri = new URI(cleanUrl);
                if (dbUri.getUserInfo() != null) {
                    String[] userInfo = dbUri.getUserInfo().split(":");
                    username = userInfo[0];
                    if (userInfo.length > 1) {
                        password = userInfo[1];
                    }
                }
                int port = dbUri.getPort() > 0 ? dbUri.getPort() : 5432;
                String host = dbUri.getHost();
                String path = dbUri.getPath();
                url = "jdbc:postgresql://" + host + ":" + port + path;
                log.info("Successfully constructed JDBC URL: {}", url);
            } catch (Exception e) {
                log.warn("Failed to parse DATABASE_URL URI, falling back to direct string format", e);
                if (!databaseUrl.startsWith("jdbc:")) {
                    url = "jdbc:" + databaseUrl;
                } else {
                    url = databaseUrl;
                }
            }
        }

        if (url == null || url.isBlank()) {
            url = "jdbc:postgresql://localhost:5432/placementor_db";
            username = "postgres";
            password = "postgres";
            log.info("Using default local PostgreSQL datasource URL: {}", url);
        }

        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}
