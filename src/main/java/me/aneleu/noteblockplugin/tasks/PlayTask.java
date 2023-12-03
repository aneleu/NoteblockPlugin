package me.aneleu.noteblockplugin.tasks;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayTask extends BukkitRunnable {

    private static final NoteblockPlugin plugin = NoteblockPlugin.plugin;

    String songName;

    public PlayTask(String songName) {
        this.songName = songName;
        // 악보 가져오기 (sheet.이름.note
    }

    @Override
    public void run() {

    }

}
