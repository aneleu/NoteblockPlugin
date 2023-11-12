package me.aneleu.noteblockplugin;

import me.aneleu.noteblockplugin.commands.NoteblockCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class NoteblockPlugin extends JavaPlugin {

    public static NoteblockPlugin plugin;

    @Override
    public void onEnable() {

        plugin = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        NoteblockCommand noteblockCommand = new NoteblockCommand(this);
        getCommand("noteblock").setExecutor(noteblockCommand);
        getCommand("noteblock").setTabCompleter(noteblockCommand);

    }

}
