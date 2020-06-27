package com.eye.constant;


import com.eye.autoAtart.AutoStart;
import com.eye.util.BiyingImage;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class Constants {

    private static boolean flag = true;

    public static String ENCOURAGE_MSG;

    public static String ENCOURAGE_MSGS;

    public static int WORKING_INTERVAL_SECONDS;
    public static int REST_INTERVAL_SECONDS;
    private static String BACK_IMAGE;
    private static String[] BACK_IMAGES;

    private static int backImageIndex = 0;

    public static int BACK_IMAGE_SAVE_INDEX = 0;

    public static boolean IS_AUTO_START = false;

    public static Properties properties = new Properties();

    public static URL resource_URL = Constants.class.getClassLoader().getResource("userconf.properties");

    public static final String ENCOURAGE_MSG_KEY = "myeye.encrourage.message";
    public static final String WORKING_INTERVAL_SECONDS_KEY = "myeye.working.interval.seconds";
    public static final String REST_INTERVAL_SECONDS_KEY = "myeye.rest.interval.seconds";
    private static final String BACK_IMAGE_KEY = "myeye.back.image.path";
    private static final String BACK_IMAGE_INDEX_KEY = "myeye.back.image.index";
    private static final String AUTO_START_KEY = "myeye.software.auto.start";


    static {
        try {
            File file = new File("userconf.properties");
            if (!file.exists()) {
                flag = true;
                properties.load(Constants.class.getClassLoader().getResourceAsStream("userconf.properties"));
            } else {
                flag = false;
                properties.load(new FileInputStream("userconf.properties"));
            }

            ENCOURAGE_MSG = properties.getProperty(ENCOURAGE_MSG_KEY).split(";")[0];
            ENCOURAGE_MSGS = properties.getProperty(ENCOURAGE_MSG_KEY);
            WORKING_INTERVAL_SECONDS = Integer.parseInt(properties.getProperty(WORKING_INTERVAL_SECONDS_KEY, String.valueOf(50 * 60)));
            REST_INTERVAL_SECONDS = Integer.parseInt(properties.getProperty(REST_INTERVAL_SECONDS_KEY, String.valueOf(50 * 60)));
            BACK_IMAGE_SAVE_INDEX = Integer.parseInt(properties.getProperty(BACK_IMAGE_INDEX_KEY, String.valueOf(0)));
            IS_AUTO_START = Boolean.parseBoolean(properties.getProperty(AUTO_START_KEY, "true"));

            BACK_IMAGE = properties.getProperty(BACK_IMAGE_KEY, "/images/water2.jpg");
            BACK_IMAGES = BACK_IMAGE.split(";");
            backImageIndex = BACK_IMAGE_SAVE_INDEX;
            IS_AUTO_START = Boolean.parseBoolean(properties.getProperty(AUTO_START_KEY, "true"));
            setBiyingBackImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getEncourageMsgs(){
        return ENCOURAGE_MSGS.split(";");
    }

    public static void saveProperties(String key, Object value) {
        properties.setProperty(key, String.valueOf(value));
        try {
            Constants.properties.store(new FileOutputStream("userconf.properties"), "v2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperties(String key) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        } else {
            return null;
        }
    }

    private static void setBiyingBackImage() {
        new Thread(() -> {
            try {
                String backImage = BiyingImage.ObtianBackImage();
                if (backImage != null) {
                    saveNewImage(backImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    private static void saveNewImage(String backImage) {
        StringBuilder newBackImages = new StringBuilder();
        if (BACK_IMAGES.length == 1 && flag) {
            newBackImages.append(backImage);
        } else if (BACK_IMAGES.length < 5) {
            newBackImages.append(backImage).append(";").append(BACK_IMAGE);
        } else {
            newBackImages.append(backImage);
            for (int i = 0; i < BACK_IMAGES.length - 1; i++) {
                newBackImages.append(";").append(BACK_IMAGES[i]);
            }
        }

        BACK_IMAGE = newBackImages.toString();
        ++BACK_IMAGE_SAVE_INDEX;
        BACK_IMAGES = BACK_IMAGE.split(";");
        Constants.saveProperties(BACK_IMAGE_KEY, BACK_IMAGE);
        Constants.saveProperties(BACK_IMAGE_INDEX_KEY, BACK_IMAGE_SAVE_INDEX);
    }

    public static String getBackImagePath() {
        backImageIndex++;
        System.out.println(BACK_IMAGES[(backImageIndex) % BACK_IMAGES.length]);
        File file = new File(BACK_IMAGES[(backImageIndex) % BACK_IMAGES.length].substring(6));
        if (file.exists()) {
            return BACK_IMAGES[(backImageIndex) % BACK_IMAGES.length];
        } else {
            return "/images/water2.jpg";
        }
    }

    public static void setAutoStart() {
        new Thread(() -> {
            boolean res = new AutoStart().setAutoStart();
            if (res) {
                saveProperties(AUTO_START_KEY, IS_AUTO_START);
            }
        }).start();
    }

}
