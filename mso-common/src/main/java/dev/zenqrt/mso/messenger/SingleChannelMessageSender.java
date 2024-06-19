package dev.zenqrt.mso.messenger;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface SingleChannelMessageSender extends MessageConnection {
    CompletableFuture<Void> sendMessage(String serverId, byte[] data);

    default CompletableFuture<Void> sendMessage(String serverId, Consumer<ByteArrayDataOutput> outputConsumer) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        outputConsumer.accept(output);

        return sendMessage(serverId, output.toByteArray());
    }
}
