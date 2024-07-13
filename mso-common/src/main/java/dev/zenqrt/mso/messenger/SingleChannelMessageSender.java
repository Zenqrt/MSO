package dev.zenqrt.mso.messenger;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface SingleChannelMessageSender extends MessageConnection {
    void sendMessage(String serverId, byte[] data);

    default void sendMessage(String serverId, Consumer<ByteArrayDataOutput> outputConsumer) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        outputConsumer.accept(output);

        sendMessage(serverId, output.toByteArray());
    }

    default CompletableFuture<Void> sendMessageAsync(String serverId, Consumer<ByteArrayDataOutput> outputConsumer) {
        return CompletableFuture.runAsync(() -> sendMessage(serverId, outputConsumer));
    }
}
