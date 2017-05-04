import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.hive.beeline.BeeLine;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 1085950 on 5/25/2016.
 */
public class HiveBeelineApplication {

    private static final String JDBC_URL = "jdbc:hive2://ebdp-ch2-e001d.sys.comcast.net:10000/dart_tv_v1";
    private static final String USERNAME = "ebdpdartd";
    private static final String PASSWORD = "Comcast02";
    private static final String DRIVER = "org.apache.hive.jdbc.HiveDriver";
    private static final String QUERY = "select count(*) from DART_TV_EVENTLOG_MASTER_ORC_V4;";
    private static final String HQL_FILE = "D:\\beeline\\create_load_caap3_bulk_top15_errors_table.hql";

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws IOException {
        mapTesting();
        /*delete();
        beeline1();
        beeline1();*/
        //beeline2();
    }

    public static void mapTesting() {
        Map<String, String> result = new HashMap<>();
        result.put("isError", "false");
        MapEditor editor =  new MapEditor(result);
        editor.editMap();
        System.out.println(result.toString());
    }

    private static void beeline1() throws FileNotFoundException, UnsupportedEncodingException {
        BeeLine beeLine = new BeeLine();
        File logFile = new File("D:\\beeline", "log1.txt");
        PrintStream beelineOutputStream = new PrintStream(logFile, "UTF-8");
        beeLine.setOutputStream(beelineOutputStream);
        beeLine.setErrorStream(beelineOutputStream);

        TailerListener listener = new MyTailerListener();
        Tailer tailer = Tailer.create(logFile, listener, 500);
        executorService.submit(tailer);

        String[] s = new String[] {"-u", JDBC_URL , "-n", USERNAME, "-p", PASSWORD, "-d", DRIVER,"--color","true", "--hiveconf", "queue=dart", "--hiveconf", "st_date=2016-03-28 00:00:00", "--hiveconf", "end_date=2016-03-31 00:00:00", "-f" , HQL_FILE};
        try {
            System.out.println(beeLine.begin(s, System.in) + "////*/********--------------*************/////////////");
            tailer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void beeline2() throws FileNotFoundException {
        BeeLine beeLine = new BeeLine();
        PrintStream beelineOutputStream = new PrintStream(new File("D:\\beeline", "log2.txt"));
        beeLine.setOutputStream(beelineOutputStream);
        beeLine.setErrorStream(beelineOutputStream);
        beeLine.runCommands(new String[] {
                "!set verbose true",
                "!set shownestederrs true",
                "!set showwarnings true",
                "!set showelapsedtime true",
                "!set maxwidth -1",
                "!connect " + JDBC_URL + " " + USERNAME + " " + PASSWORD + " " + DRIVER,
        });
        beeLine.runCommands(new String[] {
                "!set outputformat table",
                QUERY,
        });
        beeLine.runCommands(new String[] {
                "!quit"
        });
    }

    private static void delete() {
        deleteDirectory(new File("C:\\tmp\\dart_bulk_export_tmp_dir\\75e9a0f7_d614_4ed4_82d4_ef0fad0abb3f"));
    }

    private static void deleteDirectory(File folder) {
        File[] files = folder.listFiles();
        if(files != null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}

class MyTailerListener extends TailerListenerAdapter {

    public void handle(String line) {
        System.out.println("MyTailerListener :  " + line);
    }
}
