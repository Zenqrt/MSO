package dev.zenqrt.mso.messenger;

public interface MessageConnection {
    void establishConnection(String host, int port);
    void closeConnection();

    String channel();
    String serverId();
}
