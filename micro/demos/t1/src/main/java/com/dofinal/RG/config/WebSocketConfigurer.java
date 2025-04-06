package com.dofinal.RG.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Classname WebSocketConfigurer
 * Description TODO
 * Date 2024/5/28 21:15
 * Created ZHW
 */
@Configuration
public class WebSocketConfigurer {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
