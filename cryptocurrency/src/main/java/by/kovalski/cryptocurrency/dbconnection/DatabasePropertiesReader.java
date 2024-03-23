package by.kovalski.cryptocurrency.dbconnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ResourceBundle;

class DatabasePropertiesReader {
  private static final Logger logger = LogManager.getLogger();
  static final String URL;
  static final String USER;
  static final String PASSWORD;
  static final int POOL_CAPACITY;

  static {
    try {
      ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
      URL = resourceBundle.getString("url");
      USER = resourceBundle.getString("user");
      PASSWORD = resourceBundle.getString("password");
      POOL_CAPACITY = Integer.parseInt(resourceBundle.getString("pool_capacity"));
    } catch (RuntimeException e) {
      logger.fatal("Can not get DB properties", e);
      throw new ExceptionInInitializerError("Can not get DB properties");
    }
  }
}
