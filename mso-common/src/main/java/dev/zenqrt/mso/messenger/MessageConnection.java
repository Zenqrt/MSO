package dev.zenqrt.mso.messenger;

public interface MessageConnection {
    void establishConnection(String host, int port);
    void closeConnection();

    String channel();
    String serverId();

    static <T extends MessageConnection> T fromServerId(MessageConnectionFactory<T> factory) {
        return factory.createFromServerId(ConnectionSettings.SERVER_ID);
    }

    interface MessageConnectionFactory<T extends MessageConnection> {
        T createFromServerId(String serverId);
    }
}
