package me.aneleu.noteblockplugin;

import me.aneleu.noteblockplugin.commands.NoteblockCommand;
import me.aneleu.noteblockplugin.listeners.EditListener;
import me.aneleu.noteblockplugin.listeners.JoinListener;
import me.aneleu.noteblockplugin.utils.NoteblockUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
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

        ConfigurationSerialization.registerClass(NoteblockNote.class);
        getConfig().options().copyDefaults();
        saveConfig();

        getServer().getPluginManager().registerEvents(new EditListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        NoteblockCommand noteblockCommand = new NoteblockCommand();
        getCommand("noteblock").setExecutor(noteblockCommand);
        getCommand("noteblock").setTabCompleter(noteblockCommand);

        importSheetMusic();
        importEditor();

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

    private void importEditor() {
        ConfigurationSection section = getConfig().getConfigurationSection("player");
        if (section == null) {
            return;
        }

        for (String editor: section.getKeys(false)) {
            Player p = Bukkit.getPlayer(editor);
            if (p != null) {
                NoteblockUtil.startTask(Bukkit.getPlayer(editor), section.getString(editor + ".song"));
            }
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
