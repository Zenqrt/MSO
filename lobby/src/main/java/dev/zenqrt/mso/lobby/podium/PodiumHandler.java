package dev.zenqrt.mso.lobby.podium;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.zenqrt.mso.messenger.Channels;
import dev.zenqrt.mso.messenger.ConnectionSettings;
import dev.zenqrt.mso.messenger.SingleChannelMessageReceiver;
import dev.zenqrt.mso.messenger.rabbitmq.RabbitMQMessenger;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.utils.validate.Check;

import java.io.EOFException;

public final class PodiumHandler {

    private final PodiumDisplay[] displays;
    private final EventNode<Event> eventNode = EventNode.all("podiums");
    private final SingleChannelMessageReceiver infoChannelReceiver;

    public PodiumHandler(PodiumDisplay[] displays) {
        Check.argCondition(displays.length != 3, "displays should be length of 3");
        this.displays = displays;
        this.infoChannelReceiver = RabbitMQMessenger.createReceiverWithId(Channels.INFO);
    }

    public void init() {
        infoChannelReceiver.establishConnection(ConnectionSettings.HOST, ConnectionSettings.PORT);
        ConnectionSettings.createMessageReceiveListener(infoChannelReceiver, data -> {
            ByteArrayDataInput input = ByteStreams.newDataInput(data);
            String line = input.readUTF();

            if (line.equals("scores")) {
                int index = 0;

                while (true) {
                    try {
                        String _ = input.readUTF();
                        String username = input.readUTF();
                        String textureValue = input.readUTF();
                        String signature = input.readUTF();
                        int score = input.readInt();

                        displays[index++].update(username, new PlayerSkin(textureValue, signature), score);
                    } catch (RuntimeException exception) {
                        if (!(exception.getCause() instanceof EOFException)) {
                            return;
                        }

                        break;
                    }
                }
            }
        }).start();
    }

    public EventNode<Event> getEventNode() {
        return eventNode;
    }

}
