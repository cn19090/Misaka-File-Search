package space.misakacloud.Handler;

import space.misakacloud.UI.MainFrame;

import java.awt.*;

public class WindowMoveHandler extends Thread {
    int relativeX;
    int relativeY;
    public boolean flag=true;
    @Override
    public void run() {
        while (flag) {
            PointerInfo pointerInfo= MouseInfo.getPointerInfo();
            Point location = pointerInfo.getLocation();
            MainFrame.frame.setLocation(location.x - relativeX, location.y - relativeY);
        }
    }

    public WindowMoveHandler() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point location = pointerInfo.getLocation();
        Point o = MainFrame.frame.getLocation();
        relativeX = location.x - o.x;
        relativeY = location.y - o.y;
    }
}
