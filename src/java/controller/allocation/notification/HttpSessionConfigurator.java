package controller.allocation.notification;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.websocket.HandshakeResponse;

/**
 * Configurator dùng để đưa HttpSession vào userProperties của WebSocket Session,
 * cho phép endpoint lấy được currentUser từ HttpSession.
 */
public class HttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config,
                                HandshakeRequest request,
                                HandshakeResponse response) {
        super.modifyHandshake(config, request, response);

        HttpSession httpSession = (HttpSession) request.getHttpSession();
        if (httpSession != null) {
            config.getUserProperties().put(HttpSession.class.getName(), httpSession);
        }
    }
}

