package dev.zenqrt.mso.parkourrace.game.states;

import dev.zenqrt.mso.game.state.EventGameState;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;

public final class DisableCollisionGameState extends EventGameState {

    private Team noCollisionTeam;

    public DisableCollisionGameState(EventNode<Event> parentNode) {
        super(parentNode);
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(PlayerSpawnEvent.class, event -> noCollisionTeam.addMember(event.getPlayer().getUsername()));
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();

        this.noCollisionTeam = MinecraftServer.getTeamManager().createBuilder("no_collision")
                .collisionRule(TeamsPacket.CollisionRule.NEVER)
                .seeInvisiblePlayers()
                .build();
    }
}
