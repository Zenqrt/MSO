package dev.zenqrt.mso.proxy.game.state.states;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.util.GameProfile;
import dev.zenqrt.mso.messenger.ConnectionSettings;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.MSOTournamentGame;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayer;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayerList;
import dev.zenqrt.mso.proxy.game.state.EventGameState;
import dev.zenqrt.mso.proxy.utils.ConnectionUtils;

import java.io.EOFException;
import java.util.UUID;

public final class ActiveGameState extends EventGameState {

    private final MSOGame game;
    private final MSOTournamentGame tournamentGame;
    private final Thread nextStateListener;
    private final Thread updateLeaderboardListener;

    public ActiveGameState(MSOProxy plugin, MSOGame game, MSOTournamentGame tournamentGame) {
        super(plugin);

        this.game = game;
        this.tournamentGame = tournamentGame;
        this.nextStateListener = ConnectionSettings.createMessageReceiveListener(game.getGameTransferChannelReceiver(), _ -> {
            System.out.println("Received next_state");
            game.switchToNextState();
        });
        this.updateLeaderboardListener = ConnectionSettings.createMessageReceiveListener(game.getUpdateChannelReceiver(), data -> {
            ByteArrayDataInput input = ByteStreams.newDataInput(data);
            MSOGamePlayerList playerList = game.getPlayerList();

            while (true) {
                try {
                    String uuidString = input.readUTF();
                    MSOGamePlayer gamePlayer = playerList.getPlayer(UUID.fromString(uuidString));
                    int pointsEarned = input.readInt();

                    playerList.updatePlayer(gamePlayer.addScore(pointsEarned));
                } catch (RuntimeException exception) {
                    if (!(exception.getCause() instanceof EOFException)) {
                        plugin.getLogger().error("error while receiving data", exception.getCause());
                        return;
                    }

                    break;
                }
            }

            game.getLeaderboard().update();
            game.getInfoChannelSender().sendMessage("lobby", output -> {
                output.writeUTF("scores");

                for (MSOGamePlayer topPlayer : game.getLeaderboard().getTopPlayers()) {
                    if (topPlayer == null)
                        break;

                    output.writeUTF(topPlayer.uuid().toString());
                    output.writeUTF(topPlayer.player().getUsername());

                    GameProfile.Property textureProperty = topPlayer.player().getGameProfileProperties().stream()
                            .filter(property -> property.getName().equals("textures"))
                            .findFirst()
                            .orElseThrow();
                    output.writeUTF(textureProperty.getValue());
                    output.writeUTF(textureProperty.getSignature());

                    output.writeInt(topPlayer.score());
                }
            });
        });
    }


    @Override
    protected void onStateStart() {
        super.onStateStart();

        ConnectionUtils.sendAllPlayersToServer(plugin.getServer(), tournamentGame.server());
        this.nextStateListener.start();
        this.updateLeaderboardListener.start();
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();

        game.switchToNextGame();
        this.nextStateListener.interrupt();
        this.updateLeaderboardListener.interrupt();
    }
}
