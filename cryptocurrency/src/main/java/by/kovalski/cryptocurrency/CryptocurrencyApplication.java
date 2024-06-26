package by.kovalski.cryptocurrency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CryptocurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptocurrencyApplication.class, args);
	}

}
