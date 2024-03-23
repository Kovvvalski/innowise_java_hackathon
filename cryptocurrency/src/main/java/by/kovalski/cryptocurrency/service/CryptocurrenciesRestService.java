package by.kovalski.cryptocurrency.service;

import by.kovalski.cryptocurrency.api.CryptocurrenciesRestMap;
import by.kovalski.cryptocurrency.dao.CryptocurrencyDao;
import by.kovalski.cryptocurrency.exception.ApiException;
import by.kovalski.cryptocurrency.exception.DaoException;
import by.kovalski.cryptocurrency.model.Cryptocurrency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
public class CryptocurrenciesRestService {
  private static final Logger logger = LogManager.getLogger();
  private final CryptocurrenciesRestMap cryptocurrenciesRestMap;
  private final CryptocurrencyDao cryptocurrencyDao;

  public CryptocurrenciesRestService(@Autowired CryptocurrenciesRestMap cryptocurrenciesRestMap,
                                     @Autowired CryptocurrencyDao cryptocurrencyDao) {
    this.cryptocurrenciesRestMap = cryptocurrenciesRestMap;
    this.cryptocurrencyDao = cryptocurrencyDao;
  }

  @Scheduled(fixedRate = 10000)
  public void fetchCryptocurrenciesData() {
    try {
      Set<Cryptocurrency> actual = cryptocurrenciesRestMap.getActualCryptocurrencies();
      Set<Cryptocurrency> notActual = cryptocurrencyDao.findAll();
      if(notActual.isEmpty()){
        //TODO addAll, update price difference
        for (Cryptocurrency cryptocurrency : actual){
          cryptocurrencyDao.create(cryptocurrency);
        }
        return;
      }
      Map<String, Long> nameId = new HashMap<>();
      for(Cryptocurrency cryptocurrency : notActual){
        nameId.put(cryptocurrency.getName(), cryptocurrency.getId());
      }
      //temporarily moving the elements of the set into a sheet to avoid data loss, since I am going to change the elements, which will change their hash codes
      List<Cryptocurrency> actualList = new ArrayList<>(actual);
      for (Cryptocurrency cryptocurrency : actualList){
        cryptocurrency.setId(nameId.get(cryptocurrency.getName()));
      }
      actual = new HashSet<>(actualList);
      cryptocurrencyDao.updateAll(actual);
    } catch (ApiException e) {
      logger.error("Can not fetch data", e);
    } catch (DaoException e) {
      logger.error("Error during connecting to the DB");
    }
  }


}
