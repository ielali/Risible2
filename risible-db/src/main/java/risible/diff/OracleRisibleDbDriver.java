package risible.diff;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OracleRisibleDbDriver implements RisibleDbDriver {
  public List<String> getTableList(Connection dbConnect) throws SQLException {
    return getTableList(dbConnect,"");
  }

  public List<String> getTableList(Connection dbConnect, String schema) throws SQLException {
    ResultSet resultSet;
    Statement statement = dbConnect.createStatement();
    String schemaPrefix;
    if(schema==null || schema.length()==0) {
      resultSet = statement.executeQuery("select * from USER_TABLES");
      schemaPrefix = "";
    }
    else {
      resultSet = statement.executeQuery("select * from ALL_TABLES where owner = '"+schema.toUpperCase()+"'");
      schemaPrefix = schema + ".";
    }
    List<String> tableList = new ArrayList<String>();
    while (resultSet.next()) {
      tableList.add(schemaPrefix+resultSet.getString("TABLE_NAME"));
    }
    resultSet.close();
    return tableList;
  }
}
