package risible.diff;

import com.google.common.collect.MapDifference;
import risible.testsupport.RisibleDBTestCase;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Statement;

public class DBStateComparerAcceptanceTest extends RisibleDBTestCase {
  private DBStateComparer dbStateComparer;

  protected void setUp() throws Exception {
    super.setUp();
    dbStateComparer = new DBStateComparer();
    dbStateComparer.setUIDisplay(new Label());
    dbStateComparer.setDbConnection(getConnectionFour());
    dbStateComparer.setRisibleDbDriver(new OracleRisibleDbDriver());
    //dbStateComparer.setRisibleDbDriver(new MysqlRisibleDbDriver());
  }

  protected void tearDown() throws Exception {
    executeSql("delete from countrysport where country = 'IM'");
    executeSql("delete from country where code = 'IM'");
    executeSql("delete from risibleTest3.country where code = 'IM'");
  }

  public void testDetectWhenNoUpdate() throws SQLException {
    dbStateComparer.firstRecord();
    dbStateComparer.secondRecord();
    dbStateComparer.diffRecords();
    assertEquals(0,dbStateComparer.getTablesSizesDiff().entriesDiffering().size());
    assertEquals(5,dbStateComparer.getTablesSizesDiff().entriesInCommon().size());
    assertEquals("",dbStateComparer.outputVerifyTableCount());
  }

  public void testDetectWhenUpdates() throws SQLException {
    dbStateComparer.firstRecord();
    executeSql("INSERT INTO country VALUES ('IM', 'Imutable land', 'IL', 'FR')");
    executeSql("INSERT INTO countrysport VALUES ('IM', 1, NULL, NULL)");
    executeSql("INSERT INTO countrysport VALUES ('IM', 2, NULL, NULL)");
    dbStateComparer.secondRecord();
    dbStateComparer.diffRecords();
    assertEquals(2,dbStateComparer.getTablesSizesDiff().entriesDiffering().size());
    assertNotNull(dbStateComparer.getTablesSizesDiff().entriesDiffering().get("COUNTRY"));
    assertNumberOfChanges(1,dbStateComparer.getTablesSizesDiff().entriesDiffering().get("COUNTRY"));
    assertNotNull(dbStateComparer.getTablesSizesDiff().entriesDiffering().get("COUNTRYSPORT"));
    assertNumberOfChanges(2,dbStateComparer.getTablesSizesDiff().entriesDiffering().get("COUNTRYSPORT"));
    assertEquals(3,dbStateComparer.getTablesSizesDiff().entriesInCommon().size());
    assertEquals("@VerifyTableCount(\"COUNTRY=1,COUNTRYSPORT=2\")",dbStateComparer.outputVerifyTableCount());
  }

  public void testDetectWhenUpdatesOnTwoSchemas() throws SQLException {
    dbStateComparer.setSchemasToScan(risible.util.Lists.build("risibleTest3"));
    dbStateComparer.firstRecord();
    executeSql("INSERT INTO country VALUES ('IM', 'Imutable land', 'IL', 'FR')");
    executeSql("INSERT INTO countrysport VALUES ('IM', 1, NULL, NULL)");
    executeSql("INSERT INTO countrysport VALUES ('IM', 2, NULL, NULL)");
    executeSql("INSERT INTO risibleTest3.country VALUES ('IM', 'Imutable land', 'IL', 'FR')");
    dbStateComparer.secondRecord();
    dbStateComparer.diffRecords();
    assertEquals(3,dbStateComparer.getTablesSizesDiff().entriesDiffering().size());
    assertNotNull(dbStateComparer.getTablesSizesDiff().entriesDiffering().get("COUNTRY"));
    assertNumberOfChanges(1,dbStateComparer.getTablesSizesDiff().entriesDiffering().get("COUNTRY"));
    assertNotNull(dbStateComparer.getTablesSizesDiff().entriesDiffering().get("COUNTRYSPORT"));
    assertNumberOfChanges(2,dbStateComparer.getTablesSizesDiff().entriesDiffering().get("COUNTRYSPORT"));
    assertEquals(8,dbStateComparer.getTablesSizesDiff().entriesInCommon().size());
    assertNotNull(dbStateComparer.getTablesSizesDiff().entriesDiffering().get("risibleTest3.COUNTRY"));
    assertNumberOfChanges(1,dbStateComparer.getTablesSizesDiff().entriesDiffering().get("risibleTest3.COUNTRY"));
    assertEquals("@VerifyTableCount(\"COUNTRY=1,risibleTest3.COUNTRY=1,COUNTRYSPORT=2\")",dbStateComparer.outputVerifyTableCount());
  }

  private void assertNumberOfChanges(int number, MapDifference.ValueDifference<FullTableDesc> difference) {
    assertEquals(number,difference.rightValue().getTableCount()-difference.leftValue().getTableCount());
  }

  private void executeSql(String sqlQuery) throws SQLException {
    Statement statement = getConnectionFour().createStatement();
    statement.executeUpdate(sqlQuery);
    statement.close();
  }
}