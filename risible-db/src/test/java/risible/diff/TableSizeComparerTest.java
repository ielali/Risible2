package risible.diff;

import com.google.common.collect.MapDifference;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TableSizeComparerTest extends TestCase {
  private MapDifference<String,TableDesc> result;
  private String analyzedResult;

  public void testGatherTableListAndSize() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    String pass = "";
    String user = "root";
    String url = "jdbc:mysql://localhost/risibletest1";
    Connection connection = DriverManager.getConnection(url,user,pass);
    Map<String, TableDesc> tableSizeList = TableSizeComparer.scanTableSizes(connection);
    assertNotNull(tableSizeList);
    assertEquals(5,tableSizeList.size());
    assertEquals(new TableDesc("country",5),tableSizeList.get("country"));
    assertEquals(new TableDesc("sport",8),tableSizeList.get("sport"));
    assertEquals(null,tableSizeList.get("actuals"));
  }

  public void testCompareTwoEmptyTableSizeList() {
    assertNull(result);
    result = TableSizeComparer.tableSizesDiff(new HashMap<String, TableDesc>(),
            new HashMap<String, TableDesc>());
    assertNotNull(result);
    assertEquals(true, result.areEqual());
    assertEquals("", TableSizeComparer.outputTableDiff(result));
  }

  public void testCompareTwoDifferentTableSizeList() {
    Map<String, TableDesc> firstList = new HashMap<String, TableDesc>();
    firstList.put("Other",new TableDesc("Other",3));
    firstList.put("Table",new TableDesc("Table",34));
    firstList.put("Foo",new TableDesc("Foo",87));
    Map<String, TableDesc> secondList = new HashMap<String, TableDesc>();
    secondList.put("Foo",new TableDesc("Foo",87));
    secondList.put("Bar",new TableDesc("Bar",9));
    secondList.put("Other",new TableDesc("Other",7));
    result = TableSizeComparer.tableSizesDiff(firstList, secondList);
    analyzedResult = TableSizeComparer.outputTableDiff(result);
    assertNotNull(result);
    assertEquals(false, result.areEqual());

    assertEquals("-- Same sizes:\nFoo\n-- \n" +
                 "-- Exist only on first:\nTable\n-- \n" +
                 "-- Exist only on second:\nBar\n-- \n" +
                 "-- Sizes differs:\nOther\n-- \n", analyzedResult);
  }

  public void testCompareTwoIdenticalTableSizeList() {
    Map<String, TableDesc> firstList = new HashMap<String, TableDesc>();
    firstList.put("Other",new TableDesc("Other",3));
    firstList.put("Table",new TableDesc("Table",34));
    Map<String, TableDesc> secondList = new HashMap<String, TableDesc>();
    secondList.put("Table",new TableDesc("Table",34));
    secondList.put("Other",new TableDesc("Other",3));

    result = TableSizeComparer.tableSizesDiff(firstList, secondList);
    analyzedResult = TableSizeComparer.outputTableDiff(result);
    assertNotNull(result);
    assertEquals(true, result.areEqual());
    assertEquals("-- Same sizes:\nTable\nOther\n-- \n", analyzedResult);
  }
}
