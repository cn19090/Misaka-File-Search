package space.misakacloud.socket;

import com.google.gson.Gson;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import space.misakacloud.pojo.WsResponse;

import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Component
@ServerEndpoint("/data")
@Order(10)
public class DataSocket {
    public static Session session;
    public static RemoteEndpoint.Basic remoteEndpoint;
    public static Gson gson=new Gson();
    @OnOpen
    public void open(Session session){
        session=session;
        remoteEndpoint=session.getBasicRemote();
    }

    public static void send(WsResponse wsResponse){
        synchronized (remoteEndpoint){
            try {
                remoteEndpoint.sendText(gson.toJson(wsResponse));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
