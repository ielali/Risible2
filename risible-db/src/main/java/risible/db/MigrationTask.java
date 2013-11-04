package risible.db;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Sample of use for mysql:
 *
  <taskdef name="migrate" classname="risible.db.MigrationTask" classpathref="classpath-for-taskdef"/>

  <target name="migrateDb">
    <migrate user="root" password="" jdbcDriver="com.mysql.jdbc.Driver" url="jdbc:mysql://localhost/test"
             path="db_upgrade/2.x/" tableName="migrationTbl" migrationDriver="risible.db.MySqlMigrationTable"/>
  </target>
 */
public class MigrationTask extends Task {

  private String jdbcDriver;
  private String url;
  private String user;
  private String password;
  private boolean testMode;
  private String path;
  private String migrationDriver;
  private String tableName;
  private final static Logger log = Logger.getLogger(MigrationTask.class);
  private String sqlSeparator;

  public void execute() throws BuildException {
    try {
      Class.forName(jdbcDriver).newInstance();
      Connection connection = DriverManager.getConnection(url, user, password);
      Migrations migrations = new Migrations();
      migrations.setConnection(connection);

      migrations.setMigrationPath(path);
      migrations.setMigrationTable((MigrationTable) Class.forName(migrationDriver).newInstance());
      migrations.setMigrationTableName(tableName);
      migrations.setTestMode(testMode);
      migrations.setSqlSeparator(sqlSeparator);

      log.info("Ready to start migrating. Configuration is: \nTable:"+tableName+"\nPath:"+path+"\nUser:"+user+"\nUrl:"+url+"\nMigrationDriver:"+migrationDriver);
      migrations.start();
      System.out.println("Migrations done");
    } catch (InstantiationException e) {
      throw new BuildException("Cannot instanciate provided driver class: " + jdbcDriver, e);
    } catch (IllegalAccessException e) {
      throw new BuildException("Cannot instanciate provided driver class: " + jdbcDriver, e);
    } catch (ClassNotFoundException e) {
      throw new BuildException("Cannot instanciate provided driver class: " + jdbcDriver, e);
    } catch (SQLException e) {
      throw new BuildException(e);
    } catch (IOException e) {
      throw new BuildException(e);
    }
  }

    public void setPath(String path) {
    this.path = path;
  }

  public void setMigrationDriver(String migrationDriver) {
    this.migrationDriver = migrationDriver;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  public void setJdbcDriver(String jdbcDriver) {
    this.jdbcDriver = jdbcDriver;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setSqlSeparator(String sqlSeparator) {
    this.sqlSeparator = sqlSeparator;
  }
}
