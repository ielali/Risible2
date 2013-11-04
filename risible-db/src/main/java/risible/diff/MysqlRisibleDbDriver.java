package risible.diff;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlRisibleDbDriver implements RisibleDbDriver {
  public List<String> getTableList(Connection dbConnect) throws SQLException {
    return getTableList(dbConnect,"");
  }

  public List<String> getTableList(Connection dbConnect, String schema) throws SQLException {
    ResultSet resultSet;
    String schemaPrefix;
    if(schema==null || schema.length()==0) {
      resultSet = dbConnect.getMetaData().getTables(null, null, "", new String[]{"TABLE"});
      schemaPrefix = "";
    }
    else {
      resultSet = dbConnect.getMetaData().getTables(schema, null, "", new String[]{"TABLE"});
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
