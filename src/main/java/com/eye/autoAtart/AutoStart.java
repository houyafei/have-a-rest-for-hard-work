package com.eye.autoAtart;

import java.io.File;
import java.io.IOException;

public class AutoStart {


    public static void main(String[] args) {
        if (args.length>=1){
            System.out.println(new AutoStart().setAutoStart(true,args[0]));
        }
    }

    // 写入快捷方式 是否自启动，快捷方式的名称，注意后缀是lnk
    public boolean setAutoStart(boolean yesAutoStart, String lnk) {
        File f = new File(lnk);
        String p = f.getAbsolutePath();
        String startFolder = "";
        String osName = System.getProperty("os.name");

        if (osName.equals("Windows 7")
                || osName.equals("Windows 8")
                || osName.equals("Windows 10")
                || osName.equals("Windows Server 2012 R2")
                || osName.equals("Windows Server 2014 R2")
                || osName.equals("Windows Server 2016")) {
            startFolder = System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
        }
        if (osName.endsWith("Windows XP")) {
            startFolder = System.getProperty("user.home") + "\\「开始」菜单\\程序\\启动";
        }
        return setRunBySys(yesAutoStart, p, startFolder, lnk);
    }

    // 设置是否随系统启动
    private boolean setRunBySys(boolean b, String path, String path2, String lnk) {
        File file = new File(path2 + "\\" + lnk);
        Runtime run = Runtime.getRuntime();
        File f = new File(lnk);

        // 复制
        try {
            if (b) {
                // 写入
                // 判断是否隐藏，注意用系统copy布置为何隐藏文件不生效
                if (f.isHidden()) {
                    // 取消隐藏
                    try {
                        Runtime.getRuntime().exec("attrib -H \"" + path + "\"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!file.exists()) {
                    run.exec("cmd /c copy " + formatPath(path) + " " + formatPath(path2));
                }
                // 延迟0.5秒防止复制需要时间
                Thread.sleep(500);
            } else {
                // 删除
                if (file.exists()) {
                    if (file.isHidden()) {
                        // 取消隐藏
                        try {
                            Runtime.getRuntime().exec("attrib -H \"" + file.getAbsolutePath() + "\"");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(500);
                    }
                    run.exec("cmd /c del " + formatPath(file.getAbsolutePath()));
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 解决路径中空格问题
    private String formatPath(String path) {
        if (path == null) {
            return "";
        }
        return path.replaceAll(" ", "\" \"");
    }

}
