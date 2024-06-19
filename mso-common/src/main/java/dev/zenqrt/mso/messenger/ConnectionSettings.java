package dev.zenqrt.mso.messenger;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

public final class ConnectionSettings {

    public static final String SERVER_ID;
    public static final String HOST;
    public static final int PORT;

    static {
        String connectionPropertiesPath = "connection.properties";

        try {
            Properties properties = new Properties();
            properties.load(ConnectionSettings.class.getClassLoader().getResourceAsStream(connectionPropertiesPath));

            SERVER_ID = properties.getProperty("server_id");
            HOST = properties.getProperty("host");
            PORT = Integer.parseInt(properties.getProperty("port"));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private ConnectionSettings() {}

    public static Thread createMessageReceiveListener(SingleChannelMessageReceiver receiver, Consumer<byte[]> onReceived) {
        return new Thread(() -> {
            while (true) {
                receiver.receiveMessage(onReceived);
            }
        });
    }

}
