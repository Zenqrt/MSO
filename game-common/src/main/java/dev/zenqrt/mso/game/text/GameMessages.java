package dev.zenqrt.mso.game.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public final class GameMessages {

    private GameMessages() {}

    public static Component countdown(int seconds) {
        return Component.text("The game will start in {time} seconds", TextColor.color(0xfc86c1))
                .replaceText(builder -> builder.matchLiteral("{time}").replacement(Component.text(seconds, TextColor.color(0x40ed6e))));
    }

    public static Component death(String username, String deathMessage) {
        return Component.text("☠ ", NamedTextColor.RED)
                .append(Component.text(deathMessage, NamedTextColor.GRAY)
                        .replaceText(builder -> builder.matchLiteral("{username}").replacement(Component.text(username, NamedTextColor.YELLOW))));
    }

    public static Component scoreAdded(int score, String reason) {
        return Component.text("+" + score + " Score ", TextColor.color(0x73c2ff))
                .append(Component.text("(" + reason + ")", NamedTextColor.GRAY));
    }

}
