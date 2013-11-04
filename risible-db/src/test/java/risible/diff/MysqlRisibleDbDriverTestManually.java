package risible.diff;

import risible.testsupport.RisibleDBTestCase;

import java.sql.SQLException;
import java.util.List;

public class MysqlRisibleDbDriverTestManually extends RisibleDBTestCase {
  private RisibleDbDriver risibleDbDriver;

  protected void setUp() throws Exception {
    super.setUp();
    risibleDbDriver = new MysqlRisibleDbDriver();
  }

  public void testDetectAllCurrentUserTables() throws SQLException {
    List<String> tableList = risibleDbDriver.getTableList(getConnectionFour());
    assertEquals(5,tableList.size());
    assertEquals(true,tableList.contains("country"));
    assertEquals(true,tableList.contains("countrysport"));
    assertEquals(true,tableList.contains("langue"));
    assertEquals(true,tableList.contains("location"));
    assertEquals(true,tableList.contains("sport"));
  }

  public void testDetectAllSpecificUserTables() throws SQLException {
    List<String> tableList = risibleDbDriver.getTableList(getConnectionFour(),"risibleTest3");
    assertEquals(6,tableList.size());
    assertEquals(true,tableList.contains("risibleTest3.country"));
    assertEquals(true,tableList.contains("risibleTest3.countrysport"));
    assertEquals(true,tableList.contains("risibleTest3.langue"));
    assertEquals(true,tableList.contains("risibleTest3.location"));
    assertEquals(true,tableList.contains("risibleTest3.sport"));
    assertEquals(true,tableList.contains("risibleTest3.foo"));
  }
}