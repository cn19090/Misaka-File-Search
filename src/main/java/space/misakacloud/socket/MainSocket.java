package space.misakacloud.socket;

import com.google.gson.Gson;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import space.misakacloud.Handler.LocalSearch;
import space.misakacloud.Handler.WindowMoveHandler;
import space.misakacloud.UI.MainFrame;
import space.misakacloud.pojo.MVector;
import space.misakacloud.pojo.Predict;
import space.misakacloud.pojo.WsMesg;
import space.misakacloud.pojo.WsResponse;

import javax.swing.*;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ServerEndpoint("/main")
@Order(1)
public class MainSocket {
    private Gson gson = new Gson();
    private static Session session;
    private static RemoteEndpoint.Basic remoteEndpoint;
    private WindowMoveHandler mh;
    private Toolkit toolkit = MainFrame.frame.getToolkit();

    private Desktop desktop=Desktop.getDesktop();

    private LocalSearch search;

    public MainSocket() throws AWTException {
    }

    public void send(WsResponse wsResponse) {
        try {
            synchronized (remoteEndpoint) {
                remoteEndpoint.sendText(gson.toJson(wsResponse));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("lopen");
        this.session = session;
        remoteEndpoint = session.getBasicRemote();
    }

    @OnMessage
    public void onmesg(String mesg) {
        System.out.println(mesg);
        WsMesg wsMesg = gson.fromJson(mesg, WsMesg.class);
        switch (wsMesg.type) {
            case MOVE:
                if (mh != null) {
                    mh.flag = false;
                }
                mh = new WindowMoveHandler();
                mh.start();
                break;
            case STOP:
                if (mh != null) {
                    mh.flag = false;
                }
                break;
            case WINDOWS_CLOSE:
                System.exit(0);
                break;
            case WINDOWS_SMALL:
                MainFrame.frame.setExtendedState(JFrame.ICONIFIED);
                break;
            case WINDOWS_BIG:
                MainFrame.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                break;
            case SOUND_BEEP:
                toolkit.beep();
                break;
            case SEARCH:
                List<MVector<String, String>> args = wsMesg.args;
                String[] keys = {"keyword", "use_pattern", "case_unsence", "use_cache", "use_group"};
                HashMap<String, Object> map = new HashMap<>();
                args.forEach(a -> map.put(a.getId(), a.getKey()));
                Predict predict = new Predict() {
                    @Override
                    public boolean predict(File f) {
                        String keyword = (String) map.get("keyword");
                        String str = f.getName();

                        if (map.containsKey("case_unsence") && map.get("case_unsence").equals("true")) {

                        } else {
                            str = str.toLowerCase();
                            keyword = keyword.toLowerCase();
                        }

                        if (map.containsKey("use_pattern") && map.get("use_pattern").equals("true")) {
                            if (!str.matches(keyword)) {
                                return false;
                            }
                        } else {
                            if (!str.contains(keyword)) {
                                return false;
                            }

                        }

                        if (map.containsKey("use_cache") && map.get("use_cache").equals("true")) {

                        }

                        return true;
                    }
                };
                if (search != null) {
                    search.stopSearch();
                }
                search = new LocalSearch(this, new File("d:/"), predict, wsMesg.sessionId);
                search.start();

                break;
            case OPEN_FILE:
                List<MVector<String, String>> path = wsMesg.getArgs().stream().filter(a -> a.getId().equals("path")).collect(Collectors.toList());
                if (path.size() > 0) {
//                    try {
//                        desktop.open(new File(path.get(0).getKey()));
//                    } catch (IOException e) {
//                        Search.openFile(path.get(0).getKey());
//                    }
                    LocalSearch.openFile(path.get(0).getKey());
                }

                break;

        }
    }

}
