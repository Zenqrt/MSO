package dev.zenqrt.mso.messenger.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import dev.zenqrt.mso.messenger.SingleChannelMessageReceiver;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public final class RabbitMQMessageReceiver implements SingleChannelMessageReceiver {

    private final String serverId;
    private final String channelName;
    private Connection connection;
    private Channel channel;
    private String queueName;

    public RabbitMQMessageReceiver(String serverId, String channelName) {
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
            this.channel = this.connection.createChannel();

            this.channel.exchangeDeclare(channelName, "direct");

            this.queueName = channelName + "_queue";

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
    public void receiveMessage(Consumer<byte[]> onReceived) {
        DeliverCallback callback = (_, delivery) -> {
            this.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            onReceived.accept(delivery.getBody());
        };

        try {
            System.out.println("queue = " + queueName);
            this.channel.basicConsume(queueName, false, callback, _ -> {});
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
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
