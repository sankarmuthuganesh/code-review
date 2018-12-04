package RealTime.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class LogUtils {
    private static String LOG_FILE_PATH;
    public static FileOutputStream LOGGER;

    public static void setLogFilePath(String path) {
        LOG_FILE_PATH = path + "/log";
        if (!new File(path + "/").exists()) {
            new File(path).mkdir();
        }
        if (!new File(LOG_FILE_PATH + "/").exists()) {
            new File(LOG_FILE_PATH).mkdir();
        }
    }

    public static void init() {
        String currentDate = new Date().toString().replaceAll("( |:)", "_");
        try {
            LOGGER = new FileOutputStream(LOG_FILE_PATH + "/" + "LOG_" + currentDate + ".log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void end() {
        try {
            LOGGER.flush();
            LOGGER.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(String value) {
        try {
            LOGGER.write(value.concat("\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}