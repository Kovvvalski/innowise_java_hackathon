package by.kovalski.cryptocurrency.dbconnection;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ConnectionPool {
  private static final Logger logger = LogManager.getLogger();
  private static final Lock lock = new ReentrantLock();
  private static final AtomicBoolean isCreated = new AtomicBoolean(false);
  private static ConnectionPool instance;

  public static int POOL_CAPACITY = DatabasePropertiesReader.POOL_CAPACITY;

  private final Semaphore semaphore;
  private final Queue<Connection> availableConnections;
  private final Queue<Connection> occupiedConnections;

  private ConnectionPool() {
    availableConnections = new ArrayDeque<>();
    occupiedConnections = new ArrayDeque<>();
    for (int i = 0; i < POOL_CAPACITY; i++) {
      try {
        availableConnections.add(new ProxyConnection(ConnectionFactory.createConnection()));
      } catch (SQLException e) {
        logger.error("Can not create connection", e);
      }
    }
    checkPoolCondition();
    POOL_CAPACITY = availableConnections.size();
    semaphore = new Semaphore(availableConnections.size());
  }

  public static ConnectionPool getInstance() {
    if (instance == null) {
      lock.lock();
      try {
        if (!isCreated.get()) {
          instance = new ConnectionPool();
          isCreated.set(true);
        }
      } finally {
        lock.unlock();
      }
    }
    return instance;
  }

  public Connection getConnection() {
    Connection connection = null;
    try {
      semaphore.acquire();
      lock.lock();
      connection = availableConnections.poll();
      occupiedConnections.add(connection);
    } catch (InterruptedException e) {
      logger.error("Can not get connection", e);
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
    return connection;
  }

  public void releaseConnection(Connection connection) {
    if (connection instanceof ProxyConnection) {
      lock.lock();
      try {
        availableConnections.add(connection);
        occupiedConnections.remove(connection);
      } finally {
        lock.unlock();
      }
      semaphore.release();
    } else {
      logger.error("Trying to insert not ProxyConnection");
    }
  }

  public static void deregisterDrivers() {
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
      } catch (SQLException e) {
        logger.error("Can not deregister driver", e);
      }
    }
    logger.info("Drivers where deregistered");
  }

  @PreDestroy
  public void destroyPool() {
    for (Connection connection : availableConnections) {
      try {
        ((ProxyConnection) connection).getConnection().close();
      } catch (SQLException e) {
        logger.error("Can not close connection", e);
      }
    }
    if (!occupiedConnections.isEmpty()) {
      logger.warn("Leak of " + occupiedConnections.size() + " connections");
      for (Connection connection : occupiedConnections) {
        try {
          ((ProxyConnection) connection).getConnection().close();
        } catch (SQLException e) {
          logger.error("Can not close connection", e);
        }
      }
    } else {
      logger.info("Connection pool was successfully destroyed");
    }
  }


  private void checkPoolCondition() {
    if (availableConnections.isEmpty()) {
      logger.fatal("Error during initialising connection pool");
      throw new ExceptionInInitializerError("Error during initialising connection pool");
    }
    logger.info("Connection pool was successfully initialised");
  }
}