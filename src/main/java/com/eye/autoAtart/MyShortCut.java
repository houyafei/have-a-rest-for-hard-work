package com.eye.autoAtart;


import createshortcut.CreateShortCut;

import java.io.File;
import java.util.Objects;

public class MyShortCut {

    private static File targetFile = null;

//    public static void main(String[] args) {
////        boolean isSucc = CreateShortCut.createLnk("C:\\Users\\houya\\Desktop\\", "MssagePlatform.exe",
////                "C:\\Users\\houya\\Desktop\\mysoft\\MssagePlatform\\MssagePlatform.exe");
////        System.out.println(isSucc);
//        File file = new File("./");
//        getFile(file, "WorkHard*.exe");
//        if (targetFile != null) {
//            System.out.println(targetFile.getAbsolutePath());
//            System.out.println(targetFile.getParentFile().getAbsolutePath()
//                    + "\\"
//                    + targetFile.getName().substring(0, targetFile.getName().indexOf("."))
//                    + ".lnk");
//        }

//    }

    public String obtainShortCutFile() {
        File file = new File("../../");
        System.out.println(file.getAbsolutePath());
        getFile(file, "WorkHard*.exe");
        boolean isSuccess = false;
        if (targetFile != null) {
            isSuccess = CreateShortCut.createLnk(targetFile.getParentFile().getAbsolutePath(),
                    targetFile.getName().substring(0, targetFile.getName().indexOf(".")),
                    targetFile.getAbsolutePath());
        }
        return isSuccess ? targetFile.getParentFile().getAbsolutePath()
                + "\\"
                + targetFile.getName().substring(0, targetFile.getName().indexOf("."))
                + ".lnk" : null;
    }

    private static void getFile(File file, String fileName) {
        if (file.getName().matches(fileName)) {
            System.out.println(file.getAbsolutePath());
            targetFile = file;
        } else if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                getFile(listFile, fileName);
            }
        }

    }
//    CreateShortCut
}
