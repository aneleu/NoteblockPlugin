package me.aneleu.noteblockplugin.utils;

import it.unimi.dsi.fastutil.Pair;
import me.aneleu.noteblockplugin.NoteblockNote;
import me.aneleu.noteblockplugin.NoteblockPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class NoteblockUtil {

    // TODO GUI Inventory, Slot 완성하고, *** 태스크에서 PlayerSlotChangeEvent로 바꾸기 !!!! *****

    private static final NoteblockPlugin plugin = NoteblockPlugin.plugin;

    public static final NoteblockNote initialNote = new NoteblockNote("piano", 4, 0, 100);


    static final ItemStack main_slot1 = createItemStack(Material.RED_CONCRETE, "select note", NamedTextColor.AQUA);
    static final ItemStack main_slot2 = createItemStack(Material.ORANGE_CONCRETE, "select volume", NamedTextColor.AQUA);
    static final ItemStack main_slot3 = createItemStack(Material.YELLOW_CONCRETE, "multiple selection", NamedTextColor.AQUA);
    static final ItemStack main_slot4 = createItemStack(Material.GREEN_CONCRETE, "edit note", NamedTextColor.AQUA);
    static final ItemStack main_slot5 = createItemStack(Material.BLUE_CONCRETE, "edit volume", NamedTextColor.AQUA);
    static final ItemStack main_slot6 = createItemStack(Material.PURPLE_CONCRETE, "play", NamedTextColor.AQUA);
    static final ItemStack main_slot9 = createItemStack(Material.DARK_OAK_BUTTON, "", NamedTextColor.BLACK);
    static final ItemStack main_slot0 = createItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "", NamedTextColor.BLACK);

    public static ItemStack createItemStack(Material item, String name, TextColor color) {

        ItemStack itemStack = new ItemStack(item, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(name).color(color));
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }

    @SafeVarargs
    public static @NotNull Inventory createInventory(int line, String name, Pair<Integer, ItemStack> @NotNull ... items) {
        Inventory inventory = Bukkit.createInventory(null, line*9, Component.text(name));
        for (Pair<Integer, ItemStack> pair: items) {
            inventory.setItem(pair.left(), pair.right());
        }
        return inventory;
    }

    public static void giveMainItems(Player p) {

        p.getInventory().setItem(0, main_slot1);
        p.getInventory().setItem(1, main_slot2);
        p.getInventory().setItem(2, main_slot3);
        p.getInventory().setItem(3, main_slot0);
        p.getInventory().setItem(4, main_slot9);
        p.getInventory().setItem(5, main_slot0);
        p.getInventory().setItem(6, main_slot4);
        p.getInventory().setItem(7, main_slot5);
        p.getInventory().setItem(8, main_slot6);

    }

    public static void giveMultiSelectItems(Player p) {

    }

    public static void givePlayItems(Player p) {

    }

    public static void showNoteGUI(Player p) {

    }

    public static void showVolumeGUI(Player p) {

    }

    public static void showMultiSelectGUI(Player p) {

    }


    public static void startEditing(@NotNull Player p, @NotNull String song) {
        stopEditing(p);
        plugin.addEditingPlayer(p.getName(), song);
        plugin.getConfig().set("player."+p.getName() + ".song", song);
        plugin.getConfig().set("player."+p.getName() + ".note", initialNote);
        plugin.getConfig().set("player."+p.getName() + ".state", "single"); // single: 단일 음 편집 | multi: 여러 음 편집 | play_select: 실행 위치 지정 | lock: play중일때 등 상호작용 금지
        giveMainItems(p);
    }

    public static void stopEditing(Player p) {
        plugin.getConfig().set("player."+p.getName(), null);
        p.getInventory().clear();
        plugin.removeEditingPlayer(p.getName());
    }


    public static void setPlayerNote(String playerName, int octave, int note) {
        NoteblockNote noteblockNote = plugin.getConfig().getSerializable("player." + playerName + ".note", NoteblockNote.class);
        if (noteblockNote != null) {
            noteblockNote.setOctave(octave);
            noteblockNote.setNote(note);
            plugin.getConfig().set("player." + playerName + ".note", noteblockNote);
        }
    }

    public static void setPlayerVolume(String playerName, int volume) {
        NoteblockNote noteblockNote = plugin.getConfig().getSerializable("player." + playerName + ".note", NoteblockNote.class);
        if (noteblockNote != null) {
            noteblockNote.setVolume(volume);
            plugin.getConfig().set("player." + playerName + ".note", noteblockNote);
        }
    }

}
