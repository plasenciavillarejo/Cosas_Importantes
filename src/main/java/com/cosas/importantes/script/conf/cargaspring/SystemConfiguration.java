package com.cosas.importantes.script.conf.cargaspring;


import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;



/* Cuando no se usa spring-boot debemos de cargar las inyecciones de forma manual al iniciar el proceso.
 * Se hace con la siguiente configuraracion.
 * Posteriormente en el método main se realiza la carga.*/

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager", basePackages =  "es.chsegura.persistenciageiser.dao")
@ComponentScan(basePackages = { "es.chsegura.persistenciageiser" })
@PropertySource("classpath:/application.properties")
public class SystemConfiguration {

	// Jpa Configuration
	@Value("${spring.datasource.driver-class-name}")
	private String driver;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	@Value("${spring.jpa.hibernate.dialect}")
	private String dialect;

	@Bean
	public DataSource configureDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(configureDataSource());
		entityManagerFactoryBean.setPackagesToScan("es.chsegura.persistenciageiser.entity");
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		Properties jpaProperties = new Properties();
		jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, dialect);
		jpaProperties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
		entityManagerFactoryBean.setJpaProperties(jpaProperties);
		return entityManagerFactoryBean;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    	return new PropertySourcesPlaceholderConfigurer();    
    }
    
    // Evitar que nos de una excepción al ejeuctar spring batch con TaskScheduler.
    
    @Bean
    public TaskScheduler taskScheduler() {
    	return new ConcurrentTaskScheduler();
    }

}

