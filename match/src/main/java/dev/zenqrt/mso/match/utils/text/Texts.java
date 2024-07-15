package dev.zenqrt.mso.match.utils.text;

import dev.zenqrt.mso.text.Icons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Texts {

    private Texts() {}

    public static Component buildsCompleted(int buildsCompleted) {
        return Component.text("ʙᴜɪʟᴅs ᴄᴏᴍᴘʟᴇᴛᴇᴅ: ", NamedTextColor.GREEN)
                .append(Component.text(buildsCompleted, NamedTextColor.WHITE));
    }

    public static Component placement(int placement, Component displayName) {
        return Component.text(placement + ".  ", NamedTextColor.GRAY)
                .append(displayName);
    }

    public static Component score(int score) {
        return Component.text("ʀᴏᴜɴᴅ sᴄᴏʀᴇ: ", NamedTextColor.GREEN)
                .append(Component.text(score, NamedTextColor.WHITE).append(Component.space()).append(Icons.SCORE));
    }

}
