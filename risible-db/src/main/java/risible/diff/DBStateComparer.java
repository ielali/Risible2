package risible.diff;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;

public class DBStateComparer {
  private Connection dbConnection;
  private static Map<String, String> remarkableTables;

  private Map<String, FullTableDesc> firstTablesState;
  private Map<String, FullTableDesc> secondTablesState;
  private MapDifference<String, FullTableDesc> tablesSizesDiff;
  private Label resultLabel;

  public DBStateComparer() {
    schemasToScan = new ArrayList<String>();
  }

  static {
    remarkableTables = new HashMap<String, String>();
    remarkableTables.put("country", "select * from country");
    remarkableTables.put("langue", "select * from langue");
    remarkableTables.put("legal", "select * from leg_tb_leg");
  }

  public void setDbConnection(Connection dbConnection) {
    this.dbConnection = dbConnection;
  }


  public void firstRecord() throws SQLException {
    firstTablesState = scanTables(dbConnection);
    System.out.println("first state recorded");
    resultLabel.setText("first state recorded");
  }

  public void secondRecord() throws SQLException {
    secondTablesState = scanTables(dbConnection);
    System.out.println("second state recorded");
    resultLabel.setText("second state recorded");
  }

  protected Map<String, FullTableDesc> getFirstTablesState() {
    return firstTablesState;
  }

  protected Map<String, FullTableDesc> getSecondTablesState() {
    return secondTablesState;
  }

  protected MapDifference<String, FullTableDesc> getTablesSizesDiff() {
    return tablesSizesDiff;
  }

  public void diffRecords() {
    tablesSizesDiff = Maps.difference(firstTablesState, secondTablesState);
    System.out.println(outputTableDiff(tablesSizesDiff));
    System.out.println("diff outputed");
  }

  public String outputTableDiff(MapDifference<String, FullTableDesc> result) {
    StringBuilder output = new StringBuilder();
    analyzeADifference(output, result.entriesInCommon().keySet(), "-- Same sizes:", "-- ", "\n");
    analyzeADifference(output, result.entriesOnlyOnLeft().keySet(), "-- Exist only on first:", "-- ", "\n");
    analyzeADifference(output, result.entriesOnlyOnRight().keySet(), "-- Exist only on second:", "-- ", "\n");
    analyzeASizeOrHashDifference(output, result.entriesDiffering(), "-- Sizes differs (or hashs ?):");
    return output.toString();
  }

  private java.util.List<String> schemasToScan;

  public java.util.List<String> getSchemasToScan() {
    return schemasToScan;
  }

  public void setSchemasToScan(List<String> schemasToScan) {
    this.schemasToScan = schemasToScan;
  }

  public String outputVerifyTableCount() {
    if (tablesSizesDiff.entriesDiffering().size() == 0) return "";
    StringBuilder output = new StringBuilder("@VerifyTableCount(\"");
    Map<String, MapDifference.ValueDifference<FullTableDesc>> mapOfDiffs = tablesSizesDiff.entriesDiffering();
    boolean first = true;
    for (String tableName : mapOfDiffs.keySet()) {
      FullTableDesc leftTable = mapOfDiffs.get(tableName).leftValue();
      FullTableDesc rightTable = mapOfDiffs.get(tableName).rightValue();
      if (first) first = false;
      else output.append(",");
      output.append(tableName).append("=").append(rightTable.getTableCount() - leftTable.getTableCount());
    }
    output.append("\")");
    return output.toString();
  }

  private void analyzeASizeOrHashDifference(StringBuilder output, Map<String, MapDifference.ValueDifference<FullTableDesc>> mapOfDiffs, String title) {
    if (mapOfDiffs.size() > 0) output.append("-- ").append(title).append(":\n");
    for (String tableName : mapOfDiffs.keySet()) {
      FullTableDesc leftTable = mapOfDiffs.get(tableName).leftValue();
      FullTableDesc rightTable = mapOfDiffs.get(tableName).rightValue();
      output.append(tableName).append(" ").append(leftTable.getTableCount()).append(" ").append(rightTable.getTableCount()).append(outputHashesDiffs(rightTable.getRowHashs(), leftTable.getRowHashs())).append("\n");
    }
    if (mapOfDiffs.size() > 0) output.append("-- \n").append("\n");
  }

  private String outputHashesDiffs(Map<String, String> rightHashs, Map<String, String> leftHashs) {
    StringBuilder strBuild = new StringBuilder();
    MapDifference<String, String> result = Maps.difference(rightHashs, leftHashs);
    analyzeADifference(strBuild, result.entriesOnlyOnRight().keySet(), "row num deleted: ", ". ", "");
    analyzeADifference(strBuild, result.entriesOnlyOnLeft().keySet(), "row num added: ", ". ", "");
    analyzeAHashDiff(strBuild, result.entriesDiffering());
    return strBuild.toString();
  }

  private void analyzeAHashDiff(StringBuilder strBuild, Map<String, MapDifference.ValueDifference<String>> differenceMap) {
    if (differenceMap.size() > 0) strBuild.append("changes on row num: ");
    for (String row : differenceMap.keySet()) {
      strBuild.append(row);
    }
    if (differenceMap.size() > 0) strBuild.append(". ");
  }

  private void analyzeADifference(StringBuilder output, Set<String> setToAnalyze, String title, String endMessage, String separator) {
    if (setToAnalyze.size() > 0) output.append(title).append(separator);
    for (String tableName : setToAnalyze) {
      output.append(tableName).append(separator);
    }
    if (setToAnalyze.size() > 0) output.append(endMessage).append(separator);
  }

  private static Map<String, String> getRemarkableRowHashs(Connection dbConnect, String tableName) throws SQLException {
    Statement stmt = dbConnect.createStatement();
    Map<String, String> remarkableRowsHashs = new HashMap<String, String>();
    if (remarkableTables.containsKey(tableName)) {
      ResultSet result = stmt.executeQuery(remarkableTables.get(tableName));
      int rowid = 0;
      while (result.next()) {
        int hashRes = 17;
        for (int i = 1; i < result.getMetaData().getColumnCount(); i++) {
          hashRes = hashRes * 31 + convertForHash(result.getObject(i));
        }
        remarkableRowsHashs.put("" + rowid, "" + hashRes);
        rowid++;
      }
    }
    return remarkableRowsHashs;
  }

  public Map<String, FullTableDesc> scanTables(Connection dbConnect) throws SQLException {
    HashMap<String, FullTableDesc> result = new HashMap<String, FullTableDesc>();
    List<String> tableList = risibleDbDriver.getTableList(dbConnect);
    for (String schema : schemasToScan) {
      tableList.addAll(risibleDbDriver.getTableList(dbConnect,schema));
    }
    for (String tabelName : tableList) {
      result.put(tabelName, new FullTableDesc(tabelName, TableSizeComparer.getTableCount(dbConnect, tabelName), getRemarkableRowHashs(dbConnect, tabelName)));
      resultLabel.setText(tabelName);
      System.out.print(".");
    }
    return result;
  }

  private RisibleDbDriver risibleDbDriver;

  public void setRisibleDbDriver(RisibleDbDriver risibleDbDriver) {
    this.risibleDbDriver = risibleDbDriver;
  }

  private static int convertForHash(Object object) {
    return object != null ? object.hashCode() : 0;
  }

  public void saveFirstState() throws IOException {
    File fileOut = new File("firstState.bin");
    FileOutputStream out = new FileOutputStream(fileOut);
    ObjectOutputStream objStream = new ObjectOutputStream(out);
    objStream.writeObject(firstTablesState);
    objStream.close();
    System.out.println("First db state saved into " + fileOut.getPath());
  }

  public void loadFirstState() throws IOException, ClassNotFoundException {
    File fileIn = new File("firstState.bin");
    FileInputStream inputStream = new FileInputStream(fileIn);
    ObjectInputStream objStream = new ObjectInputStream(inputStream);
    firstTablesState = (Map<String, FullTableDesc>) objStream.readObject();
    objStream.close();
    System.out.println("First db state loaded from " + fileIn.getPath());
  }

  public void setUIDisplay(Label resultLabel) {
    this.resultLabel = resultLabel;
  }
}
