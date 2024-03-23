package by.kovalski.cryptocurrency.factory;

import by.kovalski.cryptocurrency.model.Cryptocurrency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class CryptocurrencyFactory {

  public Cryptocurrency createCryptocurrencyFromResultSet(ResultSet resultSet) throws SQLException {
    long id = resultSet.getLong("cryptocurrency_id");
    String name = resultSet.getString("cryptocurrency_name");
    double price = resultSet.getDouble("cryptocurrency_price");
    double difference = resultSet.getDouble("cryptocurrency_difference");
    return new Cryptocurrency(id, name, new BigDecimal(price), difference);
  }

  public Set<Cryptocurrency> createSetOfCryptocurrenciesFromResultSet(ResultSet resultSet)throws SQLException {
    Set<Cryptocurrency> users = new HashSet<>();
    while (resultSet.next()){
      Cryptocurrency user = createCryptocurrencyFromResultSet(resultSet);
      users.add(user);
    }
    return users;
  }
}
