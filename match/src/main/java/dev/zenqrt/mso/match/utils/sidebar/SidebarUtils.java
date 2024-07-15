package dev.zenqrt.mso.match.utils.sidebar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.scoreboard.Sidebar;

public final class SidebarUtils {

    private SidebarUtils() {}

    public static Sidebar createGameSidebar() {
        Sidebar sidebar = new Sidebar(Component.text("ᴍᴀᴛᴄʜ: ", NamedTextColor.YELLOW)
                .append(Component.text("ғʀᴇɴᴢʏ", NamedTextColor.AQUA)));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "footer",
                Component.text("ɴᴏ.ᴅᴏᴍᴀɪɴ.ᴄᴏᴍ", NamedTextColor.YELLOW),
                0
        ));
        return sidebar;
    }

}
