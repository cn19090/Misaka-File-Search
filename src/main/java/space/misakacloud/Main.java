package space.misakacloud;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.JavaCV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import space.misakacloud.UI.MainFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@SpringBootApplication
public class Main {
    static {
        try {
            Class claz = null;
            //6.5.1版本破解 兼容xp
//            claz =  Class.forName("com.teamdev.jxbrowser.chromium.aq");
            //6.21版本破解 默认使用最新的6.21版本
            claz =  Class.forName("com.teamdev.jxbrowser.chromium.ba");

            Field e = claz.getDeclaredField("e");
            Field f = claz.getDeclaredField("f");


            e.setAccessible(true);
            f.setAccessible(true);

            Field modifersField = Field.class.getDeclaredField("modifiers");
            modifersField.setAccessible(true);
            modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
            modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            e.set(null, new BigInteger("1"));
            f.set(null, new BigInteger("1"));
            modifersField.setAccessible(false);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("org.bytedeco.javacpp.logger", "silent");
        avutil.av_log_set_level(avutil.AV_LOG_FATAL);
        SpringApplication.run(Main.class);
        new MainFrame("File Search");
    }


}