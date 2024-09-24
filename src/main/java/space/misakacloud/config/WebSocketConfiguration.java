package space.misakacloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import space.misakacloud.socket.MainSocket;

@Configuration
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter (){
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        return exporter;
    }
}