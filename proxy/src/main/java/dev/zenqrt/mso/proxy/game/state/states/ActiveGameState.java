package dev.zenqrt.mso.proxy.game.state.states;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.zenqrt.mso.messages.ChannelIdentifiers;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.MSOTournamentGame;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayer;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayerList;
import dev.zenqrt.mso.proxy.game.state.EventGameState;
import dev.zenqrt.mso.proxy.utils.ConnectionUtils;

import java.util.UUID;

public final class ActiveGameState extends EventGameState {

    private static final MinecraftChannelIdentifier GAME_TRANSFER = MinecraftChannelIdentifier.from(ChannelIdentifiers.GAME_TRANSFER);
    private static final MinecraftChannelIdentifier UPDATE = MinecraftChannelIdentifier.from(ChannelIdentifiers.UPDATE);
    private final MSOGame game;
    private final MSOTournamentGame tournamentGame;

    public ActiveGameState(MSOProxy plugin, MSOGame game, MSOTournamentGame tournamentGame) {
        super(plugin);

        this.game = game;
        this.tournamentGame = tournamentGame;
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();

        ConnectionUtils.sendAllPlayersToServer(plugin.getServer(), tournamentGame.server());
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();

        game.switchToNextGame();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getIdentifier().equals(GAME_TRANSFER)) {
            if (getDataInput(event).readLine().equals("next_state"))
                game.switchToNextState();
        } else if (event.getIdentifier().equals(UPDATE)) {
            ByteArrayDataInput input = getDataInput(event);
            MSOGamePlayerList playerList = game.getPlayerList();

            for (String uuidString = input.readLine(); uuidString != null; uuidString = input.readLine()) {
                MSOGamePlayer gamePlayer = playerList.getPlayer(UUID.fromString(uuidString));
                int pointsEarned = input.readInt();

                playerList.updatePlayer(gamePlayer.addScore(pointsEarned));
            }

            game.getLeaderboard().update();
            game.getLobbyServer().sendPluginMessage(MinecraftChannelIdentifier.from(ChannelIdentifiers.INFO), output -> {
                output.writeUTF("scores");

                for (MSOGamePlayer topPlayer : game.getLeaderboard().getTopPlayers()) {
                    output.writeUTF(topPlayer.uuid().toString());
                    output.writeInt(topPlayer.score());
                }
            });
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static ByteArrayDataInput getDataInput(PluginMessageEvent event) {
        return ByteStreams.newDataInput(event.dataAsInputStream());
    }
}
