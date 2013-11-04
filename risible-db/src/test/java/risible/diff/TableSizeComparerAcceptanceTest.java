package risible.diff;

import com.google.common.collect.MapDifference;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TableSizeComparerAcceptanceTest extends TestCase {
  private Connection firstDB;
  private Connection secondDB;
  private Connection thirdDB;

  protected void setUp() throws Exception {
    super.setUp();
    firstDB = openMysqlConnection("jdbc:mysql://localhost/risibletest1");
    secondDB = openMysqlConnection("jdbc:mysql://localhost/risibletest2");
    thirdDB = openMysqlConnection("jdbc:mysql://localhost/risibletest3");
  }

  public void testDiffTwoConnectionsToIdenticalData() throws ClassNotFoundException, SQLException {
    TableSizeComparer tableSizeComparerNoDiff = new TableSizeComparer();
    tableSizeComparerNoDiff.setFirstDB(firstDB);
    tableSizeComparerNoDiff.setSecondDB(secondDB);
    MapDifference<String, TableDesc> result = tableSizeComparerNoDiff.diff();
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
        "sport\n-- \n", resultStr);
  }

  public void _testDiffTwoConnectionsToDataWithDifferences() throws ClassNotFoundException, SQLException {
    TableSizeComparer tableSizeComparerWithDiff = new TableSizeComparer();
    tableSizeComparerWithDiff.setFirstDB(firstDB);
    tableSizeComparerWithDiff.setSecondDB(thirdDB);
    MapDifference<String, TableDesc> result = tableSizeComparerWithDiff.diff();
    assertNotNull(result);
    String resultStr = TableSizeComparer.outputTableDiff(result);
    assertNotNull(resultStr);
    assertEquals(4,result.entriesDiffering().size());
    assertEquals(1,result.entriesInCommon().size());
    assertEquals(0,result.entriesOnlyOnLeft().size());
    assertEquals(0,result.entriesOnlyOnRight().size());

    assertEquals("-- Same sizes:\n" +
        "sport\n-- \n" +
        "-- Sizes differs:\n" +
        "langue\n" +
        "country\n" +
        "countrysport\n" +
        "location\n" +
        "-- \n", resultStr);
  }

  private Connection openMysqlConnection(String url) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    String pass = "";
    String user = "root";
    Connection connection = DriverManager.getConnection(url,user,pass);
    assertNotNull(connection);
    return connection;
  }

/*  public void testUsage()  {
    God<MyCurrency> toto = new God<MyCurrency>();
    God<Euro> godWithEuro = new God<Euro>();
    Euro euro = God.useSubCurrencyFrom(godWithEuro);
  }

  public static class God<T> {
    public static Y useSubCurrencyFrom(God<Y extends MyCurrency> godWithT) {
      return null;
    }
  }

  private class Euro extends MyCurrency {
  }

  private class MyCurrency {
  }*/
}