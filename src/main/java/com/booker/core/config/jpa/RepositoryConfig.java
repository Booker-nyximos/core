package com.booker.core.config.jpa;

import com.booker.core.annotation.UseDatasource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@UseDatasource
@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.booker.**.repository"
        }
)
public class RepositoryConfig {
    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource, ConfigurableListableBeanFactory beanFactory) {

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan(RepositoryConfig.class.getPackage().getName(), "com.booker.**");
        factoryBean.setJpaProperties(commonJpaProperty().getProperties());

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        return factoryBean;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa")
    JpaProperty commonJpaProperty() {
        return new JpaProperty();
    }
}
