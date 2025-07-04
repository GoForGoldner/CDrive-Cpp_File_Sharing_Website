package com.goforgoldner.c_drive.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    System.out.println("WS DEBUG: Configuiring Message Broker!");

    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(1);
    taskScheduler.setThreadNamePrefix("ws-heartbeat-thread-");
    taskScheduler.setDaemon(true);
    taskScheduler.initialize();

    config
        .enableSimpleBroker("/topic", "/queue")
        .setHeartbeatValue(new long[] {4000, 4000})
        .setTaskScheduler(taskScheduler);
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    System.out.println("WS DEBUG: Registering STOMP endpoints!");
    // For the current domain and the ng build + serve domains!
    // https://cdrivecpp.netlify.app", "http://localhost", "http://localhost:4200
    registry.addEndpoint("/ws").setAllowedOrigins("*");
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration
        .setSendTimeLimit(60 * 1000) // 60 seconds timeout for sending messages
        .setSendBufferSizeLimit(512 * 1024)
        .setMessageSizeLimit(128 * 1024);
  }

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    System.out.println("WS DEBUG: New WebSocket connection established!");
    System.out.println("WS DEBUG: Session ID: " + event.getMessage().getHeaders().get("simpSessionId"));
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    System.out.println("WS DEBUG: WebSocket connection disconnected!");
  }
}
