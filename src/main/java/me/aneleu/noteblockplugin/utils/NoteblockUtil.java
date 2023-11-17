package me.aneleu.noteblockplugin.utils;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import me.aneleu.noteblockplugin.tasks.EditTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class NoteblockUtil {

    private final static NoteblockPlugin plugin = NoteblockPlugin.plugin;

    public static final ItemStack slot1 = new ItemStack(Material.NOTE_BLOCK);

    static {

    }

    public static void giveItems(Player p) {



    }

    public static void startTask(Player p, String song) {
        stopTask(p);
        plugin.addEditTask(p.getName(), new EditTask(p).runTaskTimer(plugin, 0L, 1L));
        plugin.getConfig().set("player."+p.getName() + ".song", song);
    }

    public static void stopTask(Player p) {
        plugin.getConfig().set("player."+p.getName(), null);
        p.getInventory().clear();
        BukkitTask task = plugin.getEditTask(p.getName());
        if (task != null) {
            task.cancel();
        }
        plugin.removeEditTask(p.getName());
    }

}
