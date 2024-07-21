package dev.zenqrt.mso.game.text;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;

public final class Titles {

    private Titles() {}

    public static void sendDeathTitle(Audience audience) {
        audience.showTitle(Title.title(
                Component.text("YOU DIED!", NamedTextColor.RED).decorate(TextDecoration.BOLD),
                Component.text("imagine lol", NamedTextColor.GRAY)
        ));
    }

}
