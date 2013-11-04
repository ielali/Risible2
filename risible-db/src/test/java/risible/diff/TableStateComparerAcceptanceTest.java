package risible.diff;

import junit.framework.TestCase;
import risible.ConnectionHelper;

import java.sql.Connection;
import java.sql.SQLException;

public class TableStateComparerAcceptanceTest extends TestCase {
  private Connection firstDB;
  private Connection secondDB;
  private Connection thirdDB;

  protected void setUp() throws Exception {
    super.setUp();
    firstDB = ConnectionHelper.openMysqlConnection("jdbc:mysql://localhost/risibletest1");
    secondDB = ConnectionHelper.openMysqlConnection("jdbc:mysql://localhost/risibletest2");
    thirdDB = ConnectionHelper.openMysqlConnection("jdbc:mysql://localhost/risibletest3");
  }

  public void testDiffTwoConnectionsToIdenticalData() throws ClassNotFoundException, SQLException {
    DBStateComparer dbStateComparerNoDiff = new DBStateComparer();
    dbStateComparerNoDiff.setDbConnection(firstDB);
/*    MapDifference<String, TableDesc> result = dbStateComparerNoDiff.diff();
    assertNotNull(result);
    String resultStr = TableSizeComparer.outputTableDiff(result);
    assertNotNull(resultStr);
    assertEquals(0,result.entriesDiffering().size());
    assertEquals(5,result.entriesInCommon().size());
    assertEquals(0,result.entriesOnlyOnLeft().size());
    assertEquals(0,result.entriesOnlyOnRight().size());

    assertEquals("-- Same sizes:\nlangue\n" +
        "country\n" +
        "countrysport\n" +
        "location\n" +
        "sport\n-- \n", resultStr);*/
  }

  public void testDiffTwoConnectionsToDataWithDifferences() throws ClassNotFoundException, SQLException {
  }

}