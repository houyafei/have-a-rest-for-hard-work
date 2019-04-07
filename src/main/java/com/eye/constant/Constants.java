package com.eye.constant;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

//import java.util.prefs;
public class Constants {
    public static String ENCOURAGE_MSG;
    public static int WORKING_INTERVAL_SECONDS;
    public static int REST_INTERVAL_SECONDS;
    public static String BACK_IMAGE;

    public static Properties properties = new Properties();

    public static URL resource_URL = Constants.class.getClassLoader().getResource("userconf.properties");

    public static final String ENCOURAGE_MSG_KEY = "myeye.encrourage.message";
    public static final String WORKING_INTERVAL_SECONDS_KEY = "myeye.working.interval.seconds";
    public static final String REST_INTERVAL_SECONDS_KEY = "myeye.rest.interval.seconds";
    public static final String BACK_IMAGE_KEY = "myeye.back.image.path";


    static {
        try {
            properties.load(Constants.class.getClassLoader().getResourceAsStream("userconf.properties"));
            ENCOURAGE_MSG = properties.getProperty(ENCOURAGE_MSG_KEY);
            WORKING_INTERVAL_SECONDS = Integer.parseInt(properties.getProperty(WORKING_INTERVAL_SECONDS_KEY, String.valueOf(50 * 60)));
            REST_INTERVAL_SECONDS = Integer.parseInt(properties.getProperty(REST_INTERVAL_SECONDS_KEY, String.valueOf(50 * 60)));
            BACK_IMAGE = properties.getProperty(BACK_IMAGE_KEY, "/images/water2.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveProperties(String key, Object value) {
        properties.setProperty(key, String.valueOf(value));
        try {
            Constants.properties.store(new FileOutputStream(new File(Constants.resource_URL.getFile())), "v2");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
