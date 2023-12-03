package me.aneleu.noteblockplugin.listeners;

import me.aneleu.noteblockplugin.NoteblockNote;
import me.aneleu.noteblockplugin.NoteblockPlugin;
import me.aneleu.noteblockplugin.SheetMusic;
import me.aneleu.noteblockplugin.utils.NoteblockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Objects;

public class EditListener implements Listener {

    private final NoteblockPlugin plugin;

    @Contract(pure = true)
    public EditListener(NoteblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        String playerName = player.getName();
        String editingSong = plugin.getEditingSong(playerName);
        if (editingSong == null) {
            return;
        }

        List<Integer> pos = NoteblockUtil.getRaycastedInteractionPos(player);
        if (pos == null) return;

        int a = pos.get(0);
        int b = pos.get(1);
        String state = plugin.getConfig().getString("player." + playerName + ".state");
        SheetMusic sheetMusic = plugin.getSheetMusic(editingSong);

        if (Objects.requireNonNull(state).equalsIgnoreCase("single")) {
            if (e.getAction().isLeftClick()) {
                NoteblockNote note = plugin.getConfig().getSerializable("player." + playerName + ".note", NoteblockNote.class);
                sheetMusic.setNote(a, b, Objects.requireNonNull(note), true);
            } else if (e.getAction().isRightClick()) {
                sheetMusic.deleteNote(a, b, true, true);
            }

        } else if (state.equalsIgnoreCase("multi")) {

        } else if (state.equalsIgnoreCase("play_select")) {

        }

        e.setCancelled(true);

    }

}
