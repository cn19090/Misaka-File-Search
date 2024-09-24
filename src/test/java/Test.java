import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.nio.file.*;

public class Test {
    public static Java2DFrameConverter frameConverter = new Java2DFrameConverter();
    public static void WatcherTest()throws Exception{
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("d:/");
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event kind: " + event.kind() + ". File affected: " + event.context() + ".");
            }
            key.reset();
        }
    }

    public static void test(){
        Browser browser=new Browser();
        BrowserView browserView=new BrowserView(browser);

        JFrame jFrame=new JFrame();
        jFrame.setBounds(new Rectangle(200,200,800,800));
        jFrame.setVisible( true);
        jFrame.add(browserView);
        browser.loadURL("Https://www.bilibili.com");
    }
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
    public static void main(String[] args) throws Exception {
        test();

    }

}
