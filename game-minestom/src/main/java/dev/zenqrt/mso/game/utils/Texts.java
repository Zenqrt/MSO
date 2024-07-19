package dev.zenqrt.mso.game.utils;

import dev.zenqrt.mso.text.Icons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Texts {

    private Texts() {}

    public static Component placement(int placement, Component displayName) {
        return Component.text(placement + ".  ", NamedTextColor.GRAY)
                .append(displayName);
    }

    public static Component playerScore(int score) {
        return Component.text("ʏᴏᴜʀ sᴄᴏʀᴇ: ", NamedTextColor.GREEN)
                .append(Component.text(score, NamedTextColor.WHITE).append(Component.space()).append(Icons.SCORE));
    }

}
