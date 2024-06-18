package dev.zenqrt.mso.proxy.game.state.states;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.zenqrt.mso.messenger.ChannelIdentifiers;
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

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        System.out.println("Evernt");
        switch (event.getIdentifier().getId()) {
            case ChannelIdentifiers.GAME_TRANSFER -> {
                if (event.dataAsDataStream().readLine().equals("next_state")) {
                    game.switchToNextState();
                }
            }
            case ChannelIdentifiers.UPDATE -> {
                ByteArrayDataInput input = event.dataAsDataStream();
                MSOGamePlayerList playerList = game.getPlayerList();

                for (String uuidString = input.readUTF(); uuidString != null; uuidString = input.readLine()) {
                    MSOGamePlayer gamePlayer = playerList.getPlayer(UUID.fromString(uuidString));
                    int pointsEarned = input.readInt();

                    playerList.updatePlayer(gamePlayer.addScore(pointsEarned));
                }

                game.getLeaderboard().update();
                game.getLobbyServer().sendPluginMessage(MinecraftChannelIdentifier.from(ChannelIdentifiers.INFO), output -> {
                    output.writeUTF("scores");

                    for (MSOGamePlayer topPlayer : game.getLeaderboard().getTopPlayers()) {
                        if (topPlayer == null)
                            break;

                        output.writeUTF(topPlayer.uuid().toString());
                        output.writeInt(topPlayer.score());
                    }
                });
            }
        }
    }
}
