package risible.db;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import junit.framework.TestCase;

import java.io.IOException;
import java.sql.SQLException;

public class MigrationTestManual extends TestCase {
  private Migrations migrations;

  protected void setUp() throws Exception {
    super.setUp();
    migrations = new Migrations();
    migrations.setDataSource(createDataSource());
    migrations.setMigrationPath("db_upgrade/2.x/");
    migrations.setMigrationTable(new MySqlMigrationTable());
    migrations.setMigrationTableName("migrationsV2");
    migrations.setTestMode(false);
    migrations.setSqlSeparator("/");
  }

  private MysqlConnectionPoolDataSource createDataSource() {
    MysqlConnectionPoolDataSource poolDataSource = new MysqlConnectionPoolDataSource();
    poolDataSource.setUrl("jdbc:mysql://localhost/test");
    poolDataSource.setPassword("");
    poolDataSource.setUser("root");
    return poolDataSource;
  }

  public void testANothingToDoMigration() throws IOException, SQLException {
    migrations.start();
  }
}
