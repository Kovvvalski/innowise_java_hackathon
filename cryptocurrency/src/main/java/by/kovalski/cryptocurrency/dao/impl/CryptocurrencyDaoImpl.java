package by.kovalski.cryptocurrency.dao.impl;

import by.kovalski.cryptocurrency.dao.CryptocurrencyDao;
import by.kovalski.cryptocurrency.dbconnection.ConnectionPool;
import by.kovalski.cryptocurrency.exception.DaoException;
import by.kovalski.cryptocurrency.factory.CryptocurrencyFactory;
import by.kovalski.cryptocurrency.model.Cryptocurrency;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Component
public class CryptocurrencyDaoImpl implements CryptocurrencyDao {
  private static final Logger logger = LogManager.getLogger();
  private final ConnectionPool connectionPool;
  private final CryptocurrencyFactory cryptocurrencyFactory;

  private static final String CREATE_SQL = "INSERT INTO cryptocurrencies (cryptocurrency_name, cryptocurrency_price, cryptocurrency_difference) VALUES (?,?,?)";
  private static final String FIND_BY_ID_SQL = "SELECT cryptocurrency_id, cryptocurrency_name,cryptocurrency_price, cryptocurrency_difference FROM cryptocurrencies WHERE cryptocurrency_id = ?";
  private static final String FIND_BY_NAME_SQL = "SELECT cryptocurrency_id, cryptocurrency_name, cryptocurrency_price, cryptocurrency_difference FROM cryptocurrencies WHERE cryptocurrency_id = ?";
  private static final String FIND_ALL_SQL = "SELECT cryptocurrency_id, cryptocurrency_name, cryptocurrency_price, cryptocurrency_difference FROM cryptocurrencies";
  private static final String UPDATE_SQL = "UPDATE cryptocurrencies SET cryptocurrency_name = ?, cryptocurrency_price = ?, cryptocurrency_difference = ? WHERE cryptocurrency_id = ?";
  private static final String DELETE_BY_ID_SQL = "DELETE FROM cryptocurrencies WHERE cryptocurrency_id = ?";

  public CryptocurrencyDaoImpl(@Autowired ConnectionPool connectionPool,
                               @Autowired CryptocurrencyFactory cryptocurrencyFactory) {
    this.connectionPool = connectionPool;
    this.cryptocurrencyFactory = cryptocurrencyFactory;
  }

  @Override
  public boolean create(Cryptocurrency cryptocurrency) throws DaoException {
    Connection connection = connectionPool.getConnection();

    try (PreparedStatement createStatement = connection.prepareStatement(CREATE_SQL); PreparedStatement
            checkStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

      if (cryptocurrency.getId() != null) {
        checkStatement.setLong(1, cryptocurrency.getId());
        if (checkStatement.executeQuery().next()) {
          return false;
        }
      }

      createStatement.setString(1, cryptocurrency.getName());
      createStatement.setDouble(2, cryptocurrency.getPrice().doubleValue());
      createStatement.setDouble(3, cryptocurrency.getPriceDifference());
      createStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error during working with DB", e);
      throw new DaoException("Error during working with DB", e);

    } finally {
      connectionPool.releaseConnection(connection);
    }
    return true;
  }

  @Override
  public Cryptocurrency findById(long id) throws DaoException {
    Connection connection = connectionPool.getConnection();
    ResultSet resultSet = null;
    Cryptocurrency cryptocurrency = null;
    try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
      statement.setLong(1, id);
      resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        throw new DaoException("No such cryptocurrency");
      }
      cryptocurrency = cryptocurrencyFactory.createCryptocurrencyFromResultSet(resultSet);
    } catch (SQLException e) {
      logger.error("Error during working with DB", e);
      throw new DaoException("Error during working with DB", e);
    } finally {
      connectionPool.releaseConnection(connection);
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (SQLException e) {
          logger.error("Can not close RS", e);
        }
      }
    }
    return cryptocurrency;
  }

  @Override
  public Cryptocurrency findByName(String name) throws DaoException {
    Connection connection = connectionPool.getConnection();
    ResultSet resultSet = null;
    Cryptocurrency cryptocurrency = null;
    try (PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_SQL)) {
      statement.setString(1, name);
      resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        throw new DaoException("No such cryptocurrency");
      }
      cryptocurrency = cryptocurrencyFactory.createCryptocurrencyFromResultSet(resultSet);
    } catch (SQLException e) {
      logger.error("Error during working with DB", e);
      throw new DaoException("Error during working with DB", e);
    } finally {
      connectionPool.releaseConnection(connection);
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (SQLException e) {
          logger.error("Can not close RS", e);
        }
      }
    }
    return cryptocurrency;
  }

  @Override
  public Set<Cryptocurrency> findAll() throws DaoException {
    Connection connection = connectionPool.getConnection();
    ResultSet resultSet = null;
    Set<Cryptocurrency> cryptocurrencies = null;
    try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
      resultSet = statement.executeQuery();
      cryptocurrencies = cryptocurrencyFactory.createSetOfCryptocurrenciesFromResultSet(resultSet);
    } catch (SQLException e) {
      logger.error("Error during working with DB", e);
      throw new DaoException("Error during working with DB", e);
    } finally {
      connectionPool.releaseConnection(connection);
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (SQLException e) {
          logger.error("Can not close RS", e);
        }
      }
    }
    return cryptocurrencies;
  }

  @Override
  public boolean update(Cryptocurrency cryptocurrency) throws DaoException {
    Connection connection = connectionPool.getConnection();
    boolean flag = false;
    try {
      flag = executeUpdate(cryptocurrency, connection);
    } catch (SQLException e) {
      logger.error("Error during working with DB", e);
      throw new DaoException("Error during working with DB", e);
    } finally {
      connectionPool.releaseConnection(connection);
    }
    return flag;
  }

  @Override
  public int updateAll(Set<Cryptocurrency> data) throws DaoException {
    Connection connection = connectionPool.getConnection();
    int res = 0;
    try {
      for (Cryptocurrency cryptocurrency : data) {
        res += executeUpdate(cryptocurrency, connection) ? 1 : 0;
      }
    } catch (SQLException e) {
      logger.error("Error during working with DB", e);
      throw new DaoException("Error during working with DB", e);
    }
    return res;
  }

  @Override
  public boolean delete(Cryptocurrency cryptocurrency) throws DaoException {
    Connection connection = connectionPool.getConnection();
    boolean flag = true;
    try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
      statement.setLong(1, cryptocurrency.getId());
      int res = statement.executeUpdate();
      if (res != 1) {
        flag = false;
      }
    } catch (SQLException e) {
      logger.error("Error during working with DB", e);
      throw new DaoException("Error during working with DB", e);
    } finally {
      connectionPool.releaseConnection(connection);
    }
    return flag;
  }

  private boolean executeUpdate(Cryptocurrency cryptocurrency, Connection connection) throws SQLException {
    boolean flag = true;
    try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_SQL)) {
      updateStatement.setString(1, cryptocurrency.getName());
      updateStatement.setDouble(2, cryptocurrency.getPrice().doubleValue());
      updateStatement.setDouble(3, cryptocurrency.getPriceDifference());
      updateStatement.setLong(4, cryptocurrency.getId());
      int res = updateStatement.executeUpdate();
      if (res != 1) {
        flag = false;
      }
    }
    return flag;
  }
}
