package dev.zenqrt.mso.proxy.game.state.states;

import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.state.EventGameState;
import dev.zenqrt.mso.proxy.utils.ConnectionUtils;
import dev.zenqrt.mso.proxy.utils.scheduler.TaskUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.Duration;

public final class IntermissionGameState extends EventGameState {

    private final MSOGame game;
    private final int timeLength;
    private ScheduledTask timerTask;

    public IntermissionGameState(MSOProxy plugin, MSOGame game, int timeSeconds) {
        super(plugin);

        this.game = game;
        this.timeLength = timeSeconds;
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();

        System.out.println("Start intermission");
        ConnectionUtils.sendAllPlayersToServer(plugin.getServer(), game.getLobbyServer());
        game.getInfoChannelSender().sendMessageAsync("lobby", output -> {
            output.writeUTF("next_game");
            output.writeUTF(game.getCurrentGame().displayName());
        });

        timerTask = TaskUtils.createBuilder(plugin, new TimerTask(game, timeLength))
                .repeat(Duration.ofSeconds(1))
                .schedule();
    }

    @Override
    protected void onStateEnd() {
        System.out.println("Ending intermission");
        super.onStateEnd();
        timerTask.cancel();
    }

    private static class TimerTask implements Runnable {

        private final MSOGame game;
        private int timeLeft;

        TimerTask(MSOGame game, int time) {
            this.game = game;
            this.timeLeft = time;
        }

        @Override
        public void run() {
            Audience audience = game.getPlayerList().getPlayersAsAudience();

            if (timeLeft <= 0) {
                audience.sendActionBar(Component.text("sᴛᴀʀᴛɪɴɢ sᴏᴏɴ...", NamedTextColor.GRAY));
                return;
            }

            audience.sendActionBar(Component.text("ɪɴᴛᴇʀᴍɪꜱꜱɪᴏɴ: {time}", NamedTextColor.LIGHT_PURPLE)
                    .replaceText(builder -> builder.matchLiteral("{time}").replacement(Component.text(formatTime(timeLeft), NamedTextColor.GREEN))));
            timeLeft--;
        }

        private static String formatTime(int timeLeft) {
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;

            return "%d:%02d".formatted(minutes, seconds);
        }
    }
}
