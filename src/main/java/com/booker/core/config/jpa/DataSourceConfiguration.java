package com.booker.core.config.jpa;

import com.booker.core.annotation.UseDatasource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@UseDatasource
@Configuration
public class DataSourceConfiguration {

    public static final String WRITE_DATASOURCE = "writeDataSource";
    public static final String READ_DATASOURCE = "readDataSource";
    public static final String ROUTING_DATASOURCE = "routingDataSource";

    @Bean(WRITE_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.hikari.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(READ_DATASOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.hikari.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }


    @DependsOn({WRITE_DATASOURCE, READ_DATASOURCE})
    @Bean
    public DataSource routingDataSource(@Qualifier(WRITE_DATASOURCE) DataSource writeDataSource,
                                        @Qualifier(READ_DATASOURCE) DataSource readDataSource) {

        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("write", writeDataSource);
        dataSourceMap.put("read", readDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    @DependsOn({ROUTING_DATASOURCE})
    @Primary
    @Bean
    public DataSource dataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
