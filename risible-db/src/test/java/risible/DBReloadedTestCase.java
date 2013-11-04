package risible;

import junit.framework.TestCase;

import java.sql.SQLException;

public class DBReloadedTestCase extends TestCase {

  public DBReloadedTestCase() {
    try {
      DBReloader.reloadDBOnce(ConnectionHelper.openMysqlConnection("jdbc:mysql://localhost/test"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


}
