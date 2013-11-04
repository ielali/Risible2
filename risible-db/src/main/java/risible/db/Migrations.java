// Copyright 2008 Conan Dalton and Jean-Philippe Hallot
//
// This file is part of risible-db.
//
// risible-db is free software: you can redistribute it and/or modify
// it under the terms of version 3 of the GNU Lesser General Public License as published by
// the Free Software Foundation
//
// risible-db is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// Copies of the GNU General Public License and the GNU Lesser General Public License
// are distributed with this software, see /GPL.txt and /LGPL.txt at the
// root of this distribution.
//

package risible.db;

import org.apache.log4j.Logger;
import risible.util.Files;

import javax.sql.DataSource;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Migrations {
  private static final Logger log = Logger.getLogger(Migrations.class);
  private Connection connection;
  private String migrationPath;
  private MigrationTable migrationTable;
  private String migrationTableName;
  private boolean testMode;
  private String sqlSeparator = ";";

  public boolean isTestMode() {
    return testMode;
  }

  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  public void setDataSource(DataSource dataSource) throws SQLException {
    this.connection = dataSource.getConnection();
  }

  public void start() throws SQLException, IOException {
    try {
      doMigrations();
    } finally {
      connection.close();
    }
  }

  private void doMigrations() throws SQLException, IOException {
    Set installedMigrations = findInstalledMigrations();
    Set<File> migrations = findMigrations();
    for (File migration : migrations) {
      if (!installedMigrations.contains(migration.getName())) {
        doMigration(migration);
      }
    }
  }

  private Set<File> findMigrations() {
    File dir = new File(migrationPath);
    log.info("loading migrations from " + dir.getAbsolutePath());
    Set<File> migrations = new TreeSet(new Comparator<File>() {
      public int compare(File a, File b) {
        return a.getName().compareTo(b.getName());
      }
    });
    File[] migrationFiles = dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".sql");
      }
    });
    if (migrationFiles != null) {
      migrations.addAll(Arrays.asList(migrationFiles));
    }
    logAvailableMigrations(migrations);
    return migrations;
  }

  private void logAvailableMigrations(Set<File> migrations) {
    Set<String> migrationNames = new TreeSet();
    for (File migration : migrations) {
      if (isTestMode()) {
        migrationNames.add(migration.getName());
      } else {
        if (!migration.getName().toLowerCase().contains("test")) {
          migrationNames.add(migration.getName());
        }
      }
    }
    log.info("available migrations: " + migrationNames);
    System.out.println("available migrations: " + migrationNames);
  }

  public void setSqlSeparator(String sqlSeparator) {
    this.sqlSeparator = sqlSeparator;
  }

  private void doMigration(File migration) throws SQLException, IOException {
    String migrationSqlSeparator = sqlSeparator;
    log.info("migrating from " + migration);
    doUpdateQuery(migration, "insert into " + migrationTableName + " (name) values (?)");
    if(migration.getName().toLowerCase().endsWith("_slash.sql")) {
      migrationSqlSeparator = "/";
    }

    PreparedStatement ps;
    String[] migrationContent = Files.readText(migration.getAbsolutePath()).split(migrationSqlSeparator);
    for (String stmt : migrationContent) {
      if (stmt.trim().length() > 0) {
        System.out.println("statement is: " + cleanupStatement(stmt));
        ps = connection.prepareStatement(cleanupStatement(stmt));
        ps.executeUpdate();
      }
    }

    doUpdateQuery(migration, "update " + migrationTableName + " set status = 'OK' where name = ?");
  }

  private String cleanupStatement(String stmt) {
    // fix a bug in oracle driver on windows carriage return not supported for pl/sql code: simulate what it should do
    return stmt.replaceAll("\r\n", " ");
  }

  private void doUpdateQuery(File migration, String migrationUpdate) throws SQLException {
    PreparedStatement ps = connection.prepareStatement(migrationUpdate);
    ps.setString(1, migration.getName());
    ps.executeUpdate();
  }

  private Set findInstalledMigrations() throws SQLException {
    migrationTable.ensureExists(connection, migrationTableName);
    String sql = "select name from " + migrationTableName;
    ResultSet rs = connection.prepareStatement(sql).executeQuery();
    Set result = new TreeSet();
    while (rs.next()) {
      result.add(rs.getString("name"));
    }
    log.info("installed migrations: " + result);
    return result;
  }

  public void setMigrationPath(String migrationPath) {
    this.migrationPath = migrationPath;
  }

  public void setMigrationTable(MigrationTable migrationTable) {
    this.migrationTable = migrationTable;
  }

  public void setMigrationTableName(String migrationTableName) {
    this.migrationTableName = migrationTableName;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }
}
