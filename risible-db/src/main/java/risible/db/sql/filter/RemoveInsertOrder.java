package risible.db.sql.filter;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveInsertOrder {
  public static void removeInserts(List<String> tablesToRemove, Reader input, Writer output) throws IOException {
    BufferedReader reader = new BufferedReader(input);
    PrintWriter writer = new PrintWriter(output);
    while (reader.ready()) {
      String line = reader.readLine();
      if(isNotALineToRemove(line,tablesToRemove)) {
        writer.println(line);
      }
    }
  }

  private static boolean isNotALineToRemove(String line, List<String> tablesToRemove) {
    if( ! line.toLowerCase().startsWith("insert into ")) return true;
    else {
      return ! tablesToRemove.contains(extractTableNameFrom(line).toUpperCase());
    }
  }

  private static Pattern p = Pattern.compile("insert into ([^( ]*).*");
  public static String extractTableNameFrom(String line) {
    Matcher matcher = p.matcher(line.toLowerCase());
    if(matcher.matches()) {
      return matcher.group(1);
    }
    return "";
  }
}
