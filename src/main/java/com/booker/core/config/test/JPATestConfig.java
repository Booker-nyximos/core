package com.booker.core.config.test;

import com.booker.core.annotation.UseDatasource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@UseDatasource
@Configuration
@Import({DataSourceConfiguration.class, QuerydslConfiguration.class, RepositoryConfig.class})
public class JPATestConfig {

}
