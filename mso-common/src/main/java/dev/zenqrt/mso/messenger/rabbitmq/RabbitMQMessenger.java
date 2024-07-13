package dev.zenqrt.mso.messenger.rabbitmq;

import dev.zenqrt.mso.messenger.ConnectionSettings;

public final class RabbitMQMessenger {

    public static RabbitMQMessageSender createSenderWithId(String channelName) {
        return new RabbitMQMessageSender(ConnectionSettings.SERVER_ID, channelName);
    }

    public static RabbitMQMessageReceiver createReceiverWithId(String channelName) {
        return new RabbitMQMessageReceiver(ConnectionSettings.SERVER_ID, channelName);
    }

}
