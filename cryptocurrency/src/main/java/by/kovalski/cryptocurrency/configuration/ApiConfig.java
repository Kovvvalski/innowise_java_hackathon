package by.kovalski.cryptocurrency.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfig {
  @Bean(name = "restConfig")
  public RestTemplate getRestTemplate(){
    return new RestTemplate();
  }
}
