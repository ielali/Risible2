package risible.diff;

import risible.testsupport.RisibleDBTestCase;

import java.sql.SQLException;
import java.util.List;

public class OracleRisibleDbDriverTestManually extends RisibleDBTestCase {
  private RisibleDbDriver risibleDbDriver;

  protected void setUp() throws Exception {
    super.setUp();
    risibleDbDriver = new OracleRisibleDbDriver();
  }

  public void testDetectAllCurrentUserTables() throws SQLException {
    List<String> tableList = risibleDbDriver.getTableList(getConnectionFour());
    assertEquals(5,tableList.size());
    assertEquals(true,tableList.contains("country".toUpperCase()));
    assertEquals(true,tableList.contains("countrysport".toUpperCase()));
    assertEquals(true,tableList.contains("langue".toUpperCase()));
    assertEquals(true,tableList.contains("location".toUpperCase()));
    assertEquals(true,tableList.contains("sport".toUpperCase()));
  }

  public void testDetectAllSpecificUserTables() throws SQLException {
    List<String> tableList = risibleDbDriver.getTableList(getConnectionFour(),"risibleTest3");
    assertEquals(6,tableList.size());
    assertEquals(true,tableList.contains("risibleTest3.COUNTRY"));
    assertEquals(true,tableList.contains("risibleTest3.COUNTRYSPORT"));
    assertEquals(true,tableList.contains("risibleTest3.LANGUE"));
    assertEquals(true,tableList.contains("risibleTest3.LOCATION"));
    assertEquals(true,tableList.contains("risibleTest3.SPORT"));
    assertEquals(true,tableList.contains("risibleTest3.FOO"));
  }
}