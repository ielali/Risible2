package risible.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Files {
    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File aChildren : children) {
                delete(aChildren);
            }
        }
        file.delete();
    }

    public static String readText(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] content = new byte[fis.available()];
        fis.read(content);
        return new String(content);
    }

    public static File ensureDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }
}
