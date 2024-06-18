package dev.zenqrt.mso.lobby.podium;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.zenqrt.mso.lobby.PodiumDisplay;
import dev.zenqrt.mso.messenger.ChannelIdentifiers;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.utils.validate.Check;

public final class PodiumHandler {

    private final PodiumDisplay[] displays;
    private final EventNode<Event> eventNode = EventNode.all("podiums");

    public PodiumHandler(PodiumDisplay[] displays) {
        Check.argCondition(displays.length != 3, "displays should be length of 3");
        this.displays = displays;
    }

    public void init() {
        eventNode.addListener(EventListener.builder(PlayerPluginMessageEvent.class)
                .filter(event -> {
                    System.out.println("Event!!! " + event.getIdentifier());
                    return event.getIdentifier().equals(ChannelIdentifiers.INFO);
                })
                .handler(event -> {
                    ByteArrayDataInput input = ByteStreams.newDataInput(event.getMessage());

                    if (input.readLine().equals("scores")) {
                        int index = 0;
                        for (String uuidString = input.readLine(); uuidString != null; uuidString = input.readLine()) {
                            String username = input.readLine();
                            String textureValue = input.readLine();
                            String signature = input.readLine();
                            int score = input.readInt();

                            displays[index++].update(username, new PlayerSkin(textureValue, signature));
                            System.out.println("Score: " + score);
                        }
                    }
                }).build());
    }

    public EventNode<Event> getEventNode() {
        return eventNode;
    }

}
