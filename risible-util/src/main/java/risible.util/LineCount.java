package risible.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class LineCount {
    private String sort;
    private List<String> count;

    public LineCount(String[] argv) throws IllegalAccessException, InvocationTargetException {
        Argv.setArgs(argv, this, null, null);
    }

    public static void main(String[] argv) throws IOException, IllegalAccessException, InvocationTargetException {
        new LineCount(argv).run();
    }

    public void run() throws IOException {
        Map<String, FileCount> counts = new HashMap();

        for (String arg : this.count) {
            count(new File(arg), counts);
            System.out.println("=================");
            System.out.println(arg);
            List<FileCount> fc = new ArrayList(counts.values());
            if (this.sort.length() > 0) {
                FileCount.sort(fc, this.sort);
            }

            for (FileCount f : fc) {
                System.out.println(StringUtils.leftPad(f.type, 12) + " : " +
                        StringUtils.leftPad("" + f.fileCount, 4) + " files; " +
                        StringUtils.leftPad("" + f.lineCount, 6) + " lines");
            }

            System.out.println();
            counts.clear();
        }
    }

    @ShortName("c")
    public void setCount(List count) {
        this.count = count;
    }

    @ShortName("s")
    public void setSort(String sort) {
        this.sort = sort;
    }

    private static void count(File file, Map<String, FileCount> counts) throws IOException {
        if (ignore(file)) {
            return;
        }

        for (File sub : file.listFiles()) {
            if (sub.isDirectory()) {
                get(counts, "directory").add(sub);
                count(sub, counts);
            } else {
                get(counts, typeOf(sub)).add(sub);
            }
        }
    }

    private static String typeOf(File sub) {
        String[] s = sub.getName().split("\\.");
        if (s.length == 1) {
            return "no-ext";
        } else {
            return s[s.length - 1];
        }
    }

    private static FileCount get(Map<String, FileCount> counts, String s) {
        FileCount fc = counts.get(s);
        if (fc == null) {
            fc = new FileCount(s);
            counts.put(s, fc);
        }
        return fc;
    }

    private static boolean ignore(File file) throws IOException {
        return file.getCanonicalPath().contains(".svn") ||
                file.getCanonicalPath().endsWith("trunk/data") ||
                file.getCanonicalPath().endsWith("trunk/build") ||
                file.getCanonicalPath().endsWith("trunk/tmp") ||
                file.getCanonicalPath().endsWith("trunk/lib") ||
                file.getCanonicalPath().endsWith("trunk/ant-build") ||
                file.getCanonicalPath().endsWith("trunk/classes") ||
                file.getCanonicalPath().endsWith("vendor/plugins") ||
                file.getCanonicalPath().endsWith("script") ||
                file.getName().endsWith(".jar") ||
                file.getName().endsWith(".zip") ||
                file.getName().endsWith(".png") ||
                file.getName().endsWith(".log") ||
                file.getName().endsWith(".class") ||
                file.getName().endsWith("prototype.js") ||
                file.getName().endsWith("effects.js") ||
                file.getName().endsWith(".jpg") ||
                file.getName().endsWith(".gif");
    }

    private static int linesIn(File sub) throws IOException {
        if (ignore(sub)) {
            return 0;
        }

        try {
            return Files.readText(sub.getAbsolutePath()).split("\n").length;
        } catch (Throwable e) {
            System.out.println("error counting " + sub.getCanonicalPath());
            e.printStackTrace();
            return 0;
        }
    }

    static class FileCount {
        private static final Map<String, Comparator<FileCount>> sorters = new HashMap();
        public int fileCount;
        public int lineCount;
        public String type;

        public FileCount(String s) {
            this.type = s;
        }

        public void add(File file) throws IOException {
            fileCount++;
            if (!file.isDirectory()) {
                lineCount += linesIn(file);
            }
        }

        static {
            sorters.put("type", new Comparator<FileCount>() {
                public int compare(FileCount a, FileCount b) {
                    return a.type.compareTo(b.type);
                }

                public String toString() {
                    return "type";
                }
            });

            sorters.put("file-count", new Comparator<FileCount>() {
                public int compare(FileCount a, FileCount b) {
                    return b.fileCount - a.fileCount;
                }

                public String toString() {
                    return "file-count";
                }
            });

            sorters.put("line-count", new Comparator<FileCount>() {
                public int compare(FileCount a, FileCount b) {
                    return b.lineCount - a.lineCount;
                }

                public String toString() {
                    return "line-count";
                }
            });
        }

        public static void sort(List<FileCount> fc, String by) {
            Collections.sort(fc, sorters.get(by));
        }
    }
}
