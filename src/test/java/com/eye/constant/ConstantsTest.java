package com.eye.constant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class ConstantsTest {


    private Constants constants;

    @Before
    public void createConstants() {
        constants = new Constants();
    }

    @Test
    public void readKeys() {
        for (String stringPropertyName : Constants.properties.stringPropertyNames()) {
            System.out.println(stringPropertyName);
        }
    }

    @Test
    public void saveAndGet() {
        String key1 = "myeye.working.interval.seconds";
        String key2 = "myeye.rest.interval.seconds";
        String key3 = "myeye.encrourage.message";


        String v1 = String.valueOf(13);
        String v2 = String.valueOf(13);
        String v3 = "快乐工作，开心每一天fffff";

        Constants.properties.setProperty(key1, v1);
//        Constants.properties.setProperty(key2, v2);
        Constants.properties.setProperty(key3, v3);

        try {
            System.out.println(Constants.resource_URL.getFile());

            Constants.properties.store(new FileOutputStream(new File(Constants.resource_URL.getFile())), "v2");

        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(Constants.properties.getProperty(key1), v1);
        Assert.assertEquals(Constants.properties.getProperty(key2), v2);
        Assert.assertEquals(Constants.properties.getProperty(key3), v3);

    }


    @Test
    public void saveAndGet2() {
        String v1 = "i like the working";
        Constants.saveProperties(Constants.ENCOURAGE_MSG_KEY, v1);
        Assert.assertEquals(Constants.properties.getProperty(Constants.ENCOURAGE_MSG_KEY), v1);

    }
}