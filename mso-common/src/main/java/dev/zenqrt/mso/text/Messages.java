package dev.zenqrt.mso.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class Messages {

    private Messages() {}

    public static Component success(String message) {
        return createPrefixedMessage(Component.text("✔", NamedTextColor.GREEN).decorate(TextDecoration.BOLD), Component.text(message, TextColor.color(0x69ff91)));
    }

    public static Component error(String message) {
        return createPrefixedMessage(Component.text("×", TextColor.color(0xed2424)).decorate(TextDecoration.BOLD), Component.text(message, NamedTextColor.RED));
    }

    public static Component action(String message) {
        return Component.text(message, NamedTextColor.GRAY).decorate(TextDecoration.ITALIC);
    }

    private static Component createPrefixedMessage(Component character, Component message) {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                .append(character)
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
                .append(Component.space())
                .append(message);
    }

}
