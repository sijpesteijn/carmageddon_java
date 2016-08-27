package nl.carmageddon;

import nl.carmageddon.domain.Engine;
import nl.carmageddon.domain.Pwm;
import nl.carmageddon.domain.Steer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;

/**
 * @author Gijs Sijpesteijn
 */
@SpringBootApplication
@EnableWebSocket
@EnableAutoConfiguration
@ComponentScan("nl.carmageddon")
public class Carmageddon implements WebSocketConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Carmageddon.class, args);
    }

    @Bean(name = "pwm22")
    Pwm pwm22() throws IOException {
        if (System.getProperty("os.arch").contains("arm"))
            return new Pwm(42);
        return new Pwm(0);
    }

    @Bean(name = "pwm42")
    Pwm pwm42() throws IOException {
        if (System.getProperty("os.arch").contains("arm"))
            return new Pwm(22);
        return new Pwm(0);
    }

    @Bean
    Steer steer() throws IOException {
        return new Steer(pwm22());
    }

    @Bean
    Engine engine() throws IOException {
        return new Engine(pwm42());
    }

    @Bean
    public WebSocketHandler statusWebsocketHandler() {
        return new StatusWebsocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(statusWebsocketHandler(),"/status").withSockJS();
    }
}
