package risible;

import junit.framework.Assert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
  public static Connection openMysqlConnection(String url) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    String pass = "";
    String user = "root";
    Connection connection = DriverManager.getConnection(url,user,pass);
    Assert.assertNotNull(connection);
    return connection;
  }
}
