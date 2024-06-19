package dev.zenqrt.mso.messenger.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dev.zenqrt.mso.messenger.SingleChannelMessageSender;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public final class RabbitMQMessageSender implements SingleChannelMessageSender {

    private final String serverId;
    private final String channelName;
    private Connection connection;
    private Channel channel;

    public RabbitMQMessageSender(String serverId, String channelName) {
        this.serverId = serverId;
        this.channelName = channelName;
    }

    @Override
    public void establishConnection(String host, int port) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);

        try {
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();

            this.channel.exchangeDeclare(channelName, "direct");

            String queueName = channelName + "_queue";

            this.channel.queueDeclare(queueName, false, false, false, null);
            this.channel.queueBind(queueName, channelName, serverId);
        } catch (IOException | TimeoutException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.channel.close();
                this.connection.close();
            } catch (IOException | TimeoutException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    @Override
    public CompletableFuture<Void> sendMessage(String serverId, byte[] data) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.channel.basicPublish(channelName, serverId, null, data);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public String channel() {
        return channelName;
    }

    @Override
    public String serverId() {
        return serverId;
    }
}
