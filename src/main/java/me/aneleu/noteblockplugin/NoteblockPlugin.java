package me.aneleu.noteblockplugin;

import me.aneleu.noteblockplugin.commands.NoteblockCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public final class NoteblockPlugin extends JavaPlugin {

    public static NoteblockPlugin plugin;

    private final HashMap<String, SheetMusic> sheetMusicList = new HashMap<>();

    private final HashMap<String, BukkitTask> editTaskList = new HashMap<>();

    @Override
    public void onEnable() {

        plugin = this;

        getConfig().options().copyDefaults();
        saveConfig();

        NoteblockCommand noteblockCommand = new NoteblockCommand();
        getCommand("noteblock").setExecutor(noteblockCommand);
        getCommand("noteblock").setTabCompleter(noteblockCommand);

        importSheetMusic();

    }

    private void importSheetMusic() {

        List<String> sheetmusic_list = getConfig().getStringList("list");

        for (String sheetmusic: sheetmusic_list) {

            int x = getConfig().getInt("sheet." + sheetmusic + ".x");
            int y = getConfig().getInt("sheet." + sheetmusic + ".y");
            int z = getConfig().getInt("sheet." + sheetmusic + ".z");
            int length = getConfig().getInt("sheet." + sheetmusic + ".length");
            int line = getConfig().getInt("sheet." + sheetmusic + ".line");
            SheetMusic sheetMusic = new SheetMusic(sheetmusic, x, y, z, length, line);
            addSheetMusic(sheetmusic, sheetMusic);

        }

    }

    public SheetMusic getSheetMusic(String name) {
        return sheetMusicList.get(name);
    }

    public void addSheetMusic(String name, SheetMusic sheetMusic) {
        sheetMusicList.put(name, sheetMusic);
    }

    public void removeSheetMusic(String name) {
        sheetMusicList.remove(name);
    }

    @Nullable
    public BukkitTask getEditTask(String name) {
        return editTaskList.get(name);
    }

    public void addEditTask(String name, BukkitTask task) {
        editTaskList.put(name, task);
    }

    public void removeEditTask(String name) {
        editTaskList.remove(name);
    }

}
