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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OracleMigrationTable implements MigrationTable {
  private static final Logger log = Logger.getLogger(OracleMigrationTable.class);

  public void ensureExists(Connection c, String tableName) throws SQLException {
    String q = "select table_name from user_tables where table_name = upper('" + tableName + "')";
    ResultSet rs = c.prepareStatement(q).executeQuery();
    if (rs.next()) {
      return;
    }
    
    log.info("table " + tableName + " does not exist: creating it.");

    c.prepareStatement("create table " + tableName + " (name varchar2(256), status varchar(2))").executeUpdate();
  }
}
