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

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlMigrationTable implements MigrationTable {
  public void ensureExists(Connection c, String tableName) throws SQLException {
    String create = "create table if not exists " + tableName + " (name varchar(256), status varchar(2));";
    c.prepareStatement(create).executeUpdate();
  }
}
