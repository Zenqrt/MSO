package dev.zenqrt.mso.game.tasks;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.text.TextColorPresets;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;

import java.time.Duration;

public final class CountdownTask implements Runnable {

    private final GameState state;
    private final GamePlayerList<?> playerList;
    private int timeLeft;

    public CountdownTask(GameState state, GamePlayerList<?> playerList, int time) {
        this.state = state;
        this.playerList = playerList;
        this.timeLeft = time;
    }

    @Override
    public void run() {
        Audience audience = playerList.getPlayersAsAudience();

        if (timeLeft <= 0) {
            audience.clearTitle();
            state.notifyEnd();
            return;
        }

        if (timeLeft <= 10) {
            TextColor numberColor = timeLeft == 1 ? NamedTextColor.GREEN : timeLeft == 2 ? NamedTextColor.YELLOW : timeLeft == 3 ? NamedTextColor.RED : NamedTextColor.WHITE;

            audience.showTitle(Title.title(
                    Component.text("Game starts in", TextColorPresets.TEXT),
                    Component.text("- {time} -", NamedTextColor.GRAY)
                            .replaceText(builder ->
                                    builder.matchLiteral("{time}")
                                            .replacement(Component.text(timeLeft, numberColor).decorate(TextDecoration.BOLD))),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO)
            ));

            if (timeLeft <= 3) {
                audience.playSound(Sound.sound(Key.key("minecraft:block.note_block.pling"), Sound.Source.MASTER, 1, 1), Sound.Emitter.self());
            }
        }

        timeLeft--;
    }

}
