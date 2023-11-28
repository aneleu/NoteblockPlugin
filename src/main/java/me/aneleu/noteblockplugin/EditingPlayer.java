package me.aneleu.noteblockplugin;

import org.bukkit.entity.Player;

public class EditingPlayer {

    NoteblockPlugin plugin;
    Player player;
    SheetMusic sheetMusic;

    public EditingPlayer(Player player, SheetMusic sheetMusic) {

        plugin = NoteblockPlugin.plugin;
        this.player = player;
        this.sheetMusic = sheetMusic;

    }

}
