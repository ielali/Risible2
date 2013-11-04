package risible.diff;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface RisibleDbDriver {
  List<String> getTableList(Connection dbConnect) throws SQLException;

  List<String> getTableList(Connection connection, String schema) throws SQLException;
}
