package by.kovalski.cryptocurrency.dbconnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static by.kovalski.cryptocurrency.dbconnection.DatabasePropertiesReader.*;
public class ConnectionFactory {
  private static final Logger logger = LogManager.getLogger();
  static {
    try {
      DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
    } catch (SQLException e) {
      logger.fatal("Can not register JDBC driver", e);
      throw new ExceptionInInitializerError("Can not register JDBC driver");
    }
  }

  static Connection createConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
}
