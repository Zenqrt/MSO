package dev.zenqrt.mso.messenger;

import java.util.ArrayList;
import java.util.List;

public final class MessageConnectionManager {

    private final String host;
    private final int port;
    private final List<MessageConnection> connections = new ArrayList<>();

    MessageConnectionManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static MessageConnectionManager fromConnectionSettings() {
        return new MessageConnectionManager(ConnectionSettings.HOST, ConnectionSettings.PORT);
    }

    public <T extends MessageConnection> T registerConnection(T connection) {
        connections.add(connection);
        return connection;
    }

    public void establishConnections() {
        connections.forEach(connection -> connection.establishConnection(host, port));
    }

    public void closeConnections() {
        connections.forEach(MessageConnection::closeConnection);
    }


}
