package dev.zenqrt.mso.game.text;

import dev.zenqrt.mso.text.TextColorPresets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public final class GameMessages {

    private GameMessages() {}

    public static Component countdown(int seconds) {
        return Component.text("ᴛʜᴇ ɢᴀᴍᴇ ᴡɪʟʟ sᴛᴀʀᴛ ɪɴ {time} sᴇᴄᴏɴᴅs", TextColorPresets.TEXT)
                .replaceText(builder -> builder.matchLiteral("{time}").replacement(Component.text(seconds, TextColorPresets.ARGUMENT)));
    }

    public static Component death(String username, String deathMessage) {
        return Component.text("☠ ", NamedTextColor.RED)
                .append(Component.text(deathMessage, NamedTextColor.GRAY)
                        .replaceText(builder -> builder.matchLiteral("{username}").replacement(Component.text(username, NamedTextColor.YELLOW))));
    }

    public static Component scoreAdded(int score, String reason) {
        return Component.text("+" + score + " sᴄᴏʀᴇ ", TextColor.color(0x73c2ff))
                .append(Component.text("(" + reason + ")", NamedTextColor.GRAY));
    }

}
