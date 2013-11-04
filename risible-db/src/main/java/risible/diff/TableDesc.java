package risible.diff;

public class TableDesc {
  protected String tableName;
  protected int size;

  public TableDesc(String tableName, int size) {
    this.tableName = tableName;
    this.size = size;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TableDesc tableDesc = (TableDesc) o;

    if (size != tableDesc.size) return false;
    if (!tableName.equals(tableDesc.tableName)) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = tableName.hashCode();
    result = 31 * result + size;
    return result;
  }

  public String toString() {
    return tableName+" ("+size+" rows)";
  }
}
