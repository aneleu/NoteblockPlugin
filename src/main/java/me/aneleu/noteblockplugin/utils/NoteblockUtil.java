package me.aneleu.noteblockplugin.utils;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import me.aneleu.noteblockplugin.tasks.EditTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class NoteblockUtil {

    private static final NoteblockPlugin plugin = NoteblockPlugin.plugin;

    public static final String initialInstrument = "piano";
    public static final int initialOctave = 4;
    public static final int initialNote = 0;
    public static final int initialVolume = 100;



    public static final ItemStack slot1 = new ItemStack(Material.NOTE_BLOCK);

    static {

    }

    public static void giveItems(Player p) {



    }

    public static void startTask(Player p, String song) {
        stopTask(p);
        plugin.addEditTask(p.getName(), new EditTask(p).runTaskTimer(plugin, 0L, 1L));
        plugin.getConfig().set("player."+p.getName() + ".song", song);
        plugin.getConfig().set("player."+p.getName() + ".instrument", initialInstrument);
        plugin.getConfig().set("player."+p.getName() + ".octave", initialOctave);
        plugin.getConfig().set("player."+p.getName() + ".note", initialNote);
        plugin.getConfig().set("player."+p.getName() + ".volume", initialVolume);
        plugin.getConfig().set("player."+p.getName() + ".state", "single"); // single: 단일 음 편집 | multi: 여러 음 편집 | play_select: 실행 위치 지정 | lock: play중일때 등 상호작용 금지
        plugin.saveConfig();
    }

    public static void stopTask(Player p) {
        plugin.getConfig().set("player."+p.getName(), null);
        p.getInventory().clear();
        BukkitTask task = plugin.getEditTask(p.getName());
        if (task != null) {
            task.cancel();
        }
        plugin.removeEditTask(p.getName());
        plugin.saveConfig();
    }

}
