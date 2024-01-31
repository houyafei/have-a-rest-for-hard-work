# 1、设置工作时间

时间页面设置效果图：

![效果图](https://raw.githubusercontent.com/houyafei/have-a-rest-for-hard-work/master/gitImage/v2.png)

# 2、设置休息时间

锁屏休息页面效果图

![效果图](https://raw.githubusercontent.com/houyafei/have-a-rest-for-hard-work/master/gitImage/lock_page.png)


# 3、定时锁屏
锁屏状态下，如果要想强制解锁，连续点击5次鼠标即可开锁

# 4、 使用相关技术

1)、akka actor的定时执行

2)、javaFX设置界面

3)、javafx-gradle-plugin 实现打包

配置图标时，注意放图标的位置为 src/main/deploy/package/windows

4)、打包，执行如下命令
```groovy
gradle jfxNative

```

相应的包在build/jfx/native/

注意：可能出现异常提示，可以忽略，暂不影响
```text
Execution failed for task ':jfxNative'.
> java.lang.NoSuchMethodError: com.oracle.tools.packager.windows.WinAppBundler.lambda$copyMSVCDLLs$261(Ljava/nio/file/Path;)Z
```





# 5、支持所有屏幕全部锁屏

在多个显示片状态下可以实现全部锁屏

# 6、锁频壁纸

通过必应首页的图片作为锁屏壁纸。

# 7、参考
【1】[源码地址](https://github.com/houyafei/have-a-rest-for-hard-work)
【2】[java创建exe程序快捷键方式](https://blog.csdn.net/rico_zhou/article/details/80062917)

# 8、使用

直接将 WorkHard 文件夹下的所有文件全部下载，双击其中的可执行文件即可执行。

# 9、获取图片url
https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN


# 10、版本
1)、 gradle 版本5.6.1
2)、 javaFx  版本11.0.1
3)、 java 1.8
4)、 scala 2.11