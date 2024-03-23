package by.kovalski.cryptocurrency.controller;

import by.kovalski.cryptocurrency.dao.CryptocurrencyDao;
import by.kovalski.cryptocurrency.exception.DaoException;
import by.kovalski.cryptocurrency.model.Cryptocurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class TestController {

  private final CryptocurrencyDao cryptocurrencyDao;

  public TestController(@Autowired CryptocurrencyDao cryptocurrencyDao) {
    this.cryptocurrencyDao = cryptocurrencyDao;
  }

  @GetMapping("/")
  public Set<Cryptocurrency> test() {
    try {
      return cryptocurrencyDao.findAll();
    } catch (DaoException e) {
      return null;
    }
  }
}
