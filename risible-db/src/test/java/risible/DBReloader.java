package risible;

import risible.db.Migrations;
import risible.db.MySqlMigrationTable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DBReloader {
  private static DBReloader dbReloader;
  private Connection connection;
  private Migrations migrations;

  public DBReloader(Connection connection) {
    this.connection = connection;
    migrations = new Migrations();
    migrations.setConnection(connection);
    migrations.setMigrationTable(new MySqlMigrationTable());
    migrations.setMigrationPath("db_test/mysql");
    migrations.setMigrationTableName("db_reload_log");
    migrations.setTestMode(false);
  }

  public static void reloadDBOnce(Connection connection) {
    if(dbReloader==null) {
      dbReloader = new DBReloader(connection);
      try {
        dbReloader.reload();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private void reload() throws IOException, SQLException {
    migrations.start();
  }


}
