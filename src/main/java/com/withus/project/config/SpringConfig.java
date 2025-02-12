package com.withus.project.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EntityScan(basePackages = "com.withus.project.domain.members") // MemberEntity의 패키지 경로
@EnableJpaRepositories(basePackages = "com.withus.project.repository")
public class SpringConfig {
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUsername("root");
        dataSource.setPassword("12341234");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/project?useUnicode=true&characterEncoding=UTF-8");
        return dataSource;
    }
//@Bean
//public DataSource dataSource() {
//    DriverManagerDataSource dataSource = new DriverManagerDataSource();
//    dataSource.setUsername("sa");
//    dataSource.setPassword("");
//    dataSource.setDriverClassName("org.h2.Driver");
//    dataSource.setUrl("jdbc:h2:tcp://localhost/~/test"); // H2 메모리 DB
//    return dataSource;
//}

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource()); // DataSource 설정
        emf.setPackagesToScan("com.withus.project.domain"); // JPA 엔티티 클래스 경로
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter()); // Hibernate 사용
        emf.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", "update"); // 스키마 자동 업데이트
        emf.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"); // DB 방언 설정
        return emf;
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf); // 트랜잭션 매니저 설정
    }


    public EntityManager entityManager(EntityManagerFactory emf) {
        return emf.createEntityManager(); // EntityManager를 Bean으로 등록
    }
}
