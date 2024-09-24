package space.misakacloud.UI;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PermissionHandler;
import com.teamdev.jxbrowser.chromium.PermissionRequest;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.events.ConsoleEvent;
import com.teamdev.jxbrowser.chromium.events.ConsoleListener;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    public static final JFrame frame = new JFrame();

    public MainFrame(String title) {
        String url = "http://127.0.0.1:5500/index.html";
        // 谷歌内核浏览器
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);

        //禁用close功能
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //隐藏任务栏图标
//        frame.setType(JFrame.Type.UTILITY);
        view.setSize(500, 500);
        //不显示标题栏,最大化,最小化,退出按钮
        frame.setUndecorated(true);
        frame.setResizable(true);

        frame.setSize(500, 500);
        //坐标
        frame.add(view);
        //全屏显示
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // 是否显示
        frame.setVisible(true);
//        frame.setOpacity(0);
        view.setVisible(true);
        //是否在屏幕最上层显示
//        frame.setAlwaysOnTop(true);
        //加载地址
        browser.loadURL(url);
        browser.addConsoleListener(new ConsoleListener() {
            @Override
            public void onMessage(ConsoleEvent consoleEvent) {
                System.out.println(consoleEvent.getMessage());
            }
        });
        browser.setPermissionHandler(new PermissionHandler() {
            @Override
            public PermissionStatus onRequestPermission(PermissionRequest permissionRequest) {
                return PermissionStatus.GRANTED;
            }
        });
//    browser.setUserAgent("misakacloud file search");
        frame.addWindowListener(new WindowAdapter() {
            // 窗口关闭时间监听
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("窗口关闭...");
            }
        });
    }
}
