package Foundation.FFS.Configure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@ComponentScan(basePackages = {"Fundation.FFS","Foundation.FFS.WebSockets"})
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/bridge").withSockJS()
    .setClientLibraryUrl( "https://cdn.jsdelivr.net/sockjs/1.0.2/sockjs.min.js");
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
      registration.setSendBufferSizeLimit(10*1024*1024); // default 1024 -> 10MB
  }
  
}