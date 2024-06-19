package dev.zenqrt.mso.messenger;

import java.util.ArrayList;
import java.util.List;

public final class MessageConnectionManager {

    private final String serverId;
    private final String host;
    private final int port;
    private final List<MessageConnection> connections = new ArrayList<>();

    MessageConnectionManager(String serverId, String host, int port) {
        this.serverId = serverId;
        this.host = host;
        this.port = port;
    }

    public static MessageConnectionManager fromConnectionSettings() {
        return new MessageConnectionManager(ConnectionSettings.SERVER_ID, ConnectionSettings.HOST, ConnectionSettings.PORT);
    }

    public <T extends MessageConnection> T registerConnection(MessageConnection.MessageConnectionFactory<T> factory) {
        T connection = factory.createFromServerId(serverId);
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
