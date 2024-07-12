package com.booker.core.config.test;

import com.booker.core.annotation.UseDatasource;
import com.booker.core.config.jpa.DataSourceConfiguration;
import com.booker.core.config.jpa.QuerydslConfiguration;
import com.booker.core.config.jpa.RepositoryConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@UseDatasource
@TestConfiguration
@Import({DataSourceConfiguration.class, QuerydslConfiguration.class, RepositoryConfig.class})
public class JPATestConfig {

}
