package risible.testsupport;

import junit.framework.TestCase;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class RisibleDBTestCase extends TestCase {
  private Properties props;
  private Connection connection;
  private Connection connectionThird;
  private Connection connectionFour;
  private Connection connectionOne;
  private Connection connectionTwo;

  protected void setUp() throws Exception {
    super.setUp();
    props = new Properties();
    String configName = System.getProperty("configName");
    String develpper = System.getProperty("developper");
    props.load(new FileInputStream("src/test/environment/" + develpper + "/" + configName + ".properties"));
    Class.forName(props.getProperty("datasource.driver"));
    connectionFour = DriverManager.getConnection(props.getProperty("datasource.4.url"),
                                              props.getProperty("datasource.4.user"),
                                              props.getProperty("datasource.4.password"));
    connectionOne = DriverManager.getConnection(props.getProperty("datasource.1.url"),
                                              props.getProperty("datasource.1.user"),
                                              props.getProperty("datasource.1.password"));
    connectionTwo = DriverManager.getConnection(props.getProperty("datasource.2.url"),
                                              props.getProperty("datasource.2.user"),
                                              props.getProperty("datasource.2.password"));
    connectionThird = DriverManager.getConnection(props.getProperty("datasource.3.url"),
                                              props.getProperty("datasource.3.user"),
                                              props.getProperty("datasource.3.password"));
  }

  public Properties getProperties() {
    return props;
  }

  public Connection getConnection() {
    return connection;
  }

  public Connection getConnectionThird() {
    return connectionThird;
  }

  public Connection getConnectionFour() {
    return connectionFour;
  }

  public Connection getConnectionOne() {
    return connectionOne;
  }

  public Connection getConnectionTwo() {
    return connectionTwo;
  }
}
