package me.aneleu.noteblockplugin.utils;

import net.kyori.adventure.text.Component;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Messages {

    private static @NotNull Component message(String msg, TextColor color) {
        return Component.text(msg, color);
    }

    public static final Component SONG_ALREADY_EXIST = message("That song already exists.", RED);
    public static final Component SONG_NONEXIST = message("There is no song with that title.", RED);
    public static final Component SUGGESTION_MAIN = message("/noteblock <create / remove / generate / edit / save / reduce>", GRAY);
    public static final Component SUGGESTION_EDIT = message("/noteblock edit <start / stop>", GRAY);
    public static final Component SUGGESTION_REDUCE = message("/noteblock reduce <line / length> <num>", GRAY);
    public static final Component ONLY_PLAYER = message("Players can execute this command.", RED);

}