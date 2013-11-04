package risible.diff;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TableSizeComparer {
  private Connection firstDB;
  private Connection secondDB;

  public MapDifference<String, TableDesc> diff() throws SQLException {
    Map<String, TableDesc> firstTableSizeList = scanTableSizes(firstDB);
    Map<String, TableDesc> secondTableSizeList = scanTableSizes(secondDB);
    return tableSizesDiff(firstTableSizeList, secondTableSizeList);
  }

  public static MapDifference<String, TableDesc> tableSizesDiff(Map<String, TableDesc> firstTableSizeList, Map<String, TableDesc> secondTableSizeList) {
    return Maps.difference(firstTableSizeList, secondTableSizeList);
  }

  public static Map<String, TableDesc> scanTableSizes(Connection dbConnect) throws SQLException {
    HashMap<String, TableDesc> result = new HashMap<String, TableDesc>();
    ResultSet resulset = dbConnect.getMetaData().getTables(null,null,"",new String[]{"TABLE"});
    while(resulset.next()) {
      String tabelName = resulset.getString("TABLE_NAME");
      result.put(tabelName,new TableDesc(tabelName,getTableCount(dbConnect,tabelName)));
    }
    return result;
  }

  public static int getTableCount(Connection dbConnect, String tabelName) throws SQLException {
    Statement stmt = dbConnect.createStatement();
    ResultSet result = stmt.executeQuery("select count(*) from " + tabelName);
    int count=-1;
    while (result.next()) {
      count = result.getInt(1);
    }
    result.close();
    stmt.close();
    return count;
  }

  public void setFirstDB(Connection firstDB) {
    this.firstDB = firstDB;
  }

  public void setSecondDB(Connection secondDB) {
    this.secondDB = secondDB;
  }

  public static String outputTableDiff(MapDifference<String, TableDesc> result) {
    StringBuilder output = new StringBuilder();
    analyzeADifference(output, result.entriesInCommon().keySet(), "Same sizes");
    analyzeADifference(output, result.entriesOnlyOnLeft().keySet(), "Exist only on first");
    analyzeADifference(output, result.entriesOnlyOnRight().keySet(), "Exist only on second");
    analyzeADifference(output, result.entriesDiffering().keySet(), "Sizes differs");
    return output.toString();
  }

  private static void analyzeADifference(StringBuilder output, Set<String> setToAnalyze, String title) {
    if (setToAnalyze.size() > 0) output.append("-- " + title + ":\n");
    for (String tableName : setToAnalyze) {
      output.append(tableName + "\n");
    }
    if (setToAnalyze.size() > 0) output.append("-- \n");
  }
}
