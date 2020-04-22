package com.pzl.program.frametool.mysql.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DruidConfigDataSource {

    @Primary
    @Bean(name = "dataSource1")
    @ConfigurationProperties(prefix = "mysql.druid.db1")
    public DataSource dataSourceCmuser() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "dataSource2")
    @ConfigurationProperties(prefix = "mysql.druid.db2")
    public DataSource dataSourceIrms() {
        return DruidDataSourceBuilder.create().build();
    }

}
