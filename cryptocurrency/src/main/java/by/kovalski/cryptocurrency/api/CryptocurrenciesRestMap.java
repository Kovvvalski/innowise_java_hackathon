package by.kovalski.cryptocurrency.api;

import by.kovalski.cryptocurrency.exception.ApiException;
import by.kovalski.cryptocurrency.model.Cryptocurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CryptocurrenciesRestMap {
  private static final String API_ADDRESS = "https://api.mexc.com/api/v3/ticker/price";
  private final RestTemplate restTemplate;

  public CryptocurrenciesRestMap(@Autowired RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Set<Cryptocurrency> getActualCryptocurrencies() throws ApiException {
    ResponseEntity<Cryptocurrency[]> response = restTemplate.exchange(API_ADDRESS, HttpMethod.GET,
            null, Cryptocurrency[].class);
    if (response.getBody() != null) {
      return new HashSet<>(Arrays.asList(response.getBody()));
    }
    throw new ApiException("Error during requesting to the " + API_ADDRESS);
  }
}
