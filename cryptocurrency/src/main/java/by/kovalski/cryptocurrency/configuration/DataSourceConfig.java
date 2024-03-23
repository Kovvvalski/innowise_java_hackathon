package by.kovalski.cryptocurrency.configuration;

import by.kovalski.cryptocurrency.dbconnection.ConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {
  @Bean(name = "connectionPool")
  public ConnectionPool getConnectionPool() {
    return ConnectionPool.getInstance();
  }
}
