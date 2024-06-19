package dev.zenqrt.mso.messenger;

import java.util.function.Consumer;

public interface SingleChannelMessageReceiver extends MessageConnection {
    void receiveMessage(Consumer<byte[]> onReceived);
}
