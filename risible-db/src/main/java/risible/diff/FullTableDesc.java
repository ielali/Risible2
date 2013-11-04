package risible.diff;

import java.util.Map;

public class FullTableDesc {
  private String tableName;
  private int tableCount;
  private Map<String, String> rowHashs;

  public FullTableDesc(String tableName, int tableCount, Map<String,String> rowHashs) {
    this.tableName = tableName;
    this.tableCount = tableCount;
    this.rowHashs = rowHashs;
  }

  public Map<String, String> getRowHashs() {
    return rowHashs;
  }

  public void setRowHashs(Map<String, String> rowHashs) {
    this.rowHashs = rowHashs;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public int getTableCount() {
    return tableCount;
  }

  public void setTableCount(int tableCount) {
    this.tableCount = tableCount;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FullTableDesc that = (FullTableDesc) o;

    if (tableCount != that.tableCount) return false;
    if (rowHashs != null ? !rowHashs.equals(that.rowHashs) : that.rowHashs != null) return false;
    if (tableName != null ? !tableName.equals(that.tableName) : that.tableName != null) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = (tableName != null ? tableName.hashCode() : 0);
    result = 31 * result + tableCount;
    result = 31 * result + (rowHashs != null ? rowHashs.hashCode() : 0);
    return result;
  }

  public String toString() {
    return tableName +" ("+tableCount+" rows, hash established: "+(rowHashs==null?"yes":"no")+", hash on all rows: "+(hashCalculatedOnAllRows())+")";
  }

  private String hashCalculatedOnAllRows() {
    return rowHashs==null?"unknown":(rowHashs.size()==tableCount?"yes":"no");
  }
}
