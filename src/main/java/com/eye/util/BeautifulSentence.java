package com.eye.util;



import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Yafei
 * 2022/1/20
 */
public class BeautifulSentence {


    public static String getSentence(String pythonFilePath){
        String result = "";
        try {
            String[] args1 = new String[] { "python3", pythonFilePath };
            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件

            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result = new String(Base64.getDecoder().decode(line.getBytes()), StandardCharsets.UTF_8);
//                result = new String(new BASE64Decoder().decodeBuffer(line),"utf-8");
            }
            in.close();
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }
}
