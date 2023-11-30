package me.aneleu.noteblockplugin;

import org.bukkit.entity.Player;

public class Editor {

    Player player;
    SheetMusic editingSong;
    String editingSongName;

    public Editor(Player player, SheetMusic editingSong) {
        this.player = player;
        this.editingSong = editingSong;
        editingSongName = editingSong.getName();
    }




}
