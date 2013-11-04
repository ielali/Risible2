package risible.db.sql.filter;

import junit.framework.TestCase;
import risible.util.Files;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RemoveInsertOrderTest extends TestCase {
    public void testGetTableeName() {
        String tableName = RemoveInsertOrder.extractTableNameFrom("insert into Toto(ColNam,etc) values ('irtu','sdfg');");
        assertEquals("toto", tableName);
    }

    public void testTransformFile() throws IOException {
        URL data = getClass().getClassLoader().getResource("data");
        String outputFilename = data.getFile() + "/output.sql";
        FileWriter writer = new FileWriter(outputFilename);
        FileReader reader = new FileReader(data.getFile() + "/input.sql");
        List<String> tables = new ArrayList<String>();
        tables.add("BAR");
        tables.add("PIPOPOIL");
        RemoveInsertOrder.removeInserts(tables, reader, writer);
        reader.close();
        writer.close();

        String content = Files.readText(outputFilename);
        String expectedContent = Files.readText(data.getFile() + "/outputExpected.sql");
        assertEquals(expectedContent, content);
    }
}
