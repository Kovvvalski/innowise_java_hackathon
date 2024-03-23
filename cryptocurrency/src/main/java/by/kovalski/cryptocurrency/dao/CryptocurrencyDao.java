package by.kovalski.cryptocurrency.dao;

import by.kovalski.cryptocurrency.exception.DaoException;
import by.kovalski.cryptocurrency.model.Cryptocurrency;

import java.util.Set;

public interface CryptocurrencyDao extends BaseDao<Cryptocurrency> {
  Cryptocurrency findByName(String name) throws DaoException;
  int updateAll(Set<Cryptocurrency> data) throws DaoException;
}
