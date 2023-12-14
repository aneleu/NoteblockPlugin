package me.aneleu.noteblockplugin.listeners;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import me.aneleu.noteblockplugin.utils.NoteblockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.List;

public class SlotListener implements Listener {

    NoteblockPlugin plugin = NoteblockPlugin.plugin;

    @EventHandler
    public void onPlayerInventorySlotChange(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        if (!plugin.isEditing(player.getName())) {
            return;
        }

        int slot = e.getNewSlot();

        if (slot == 3) {
            List<Integer> raycastPos = NoteblockUtil.getRaycastedInteractionPos(player);
            if (raycastPos != null) {
                plugin.getSheetMusic(plugin.getEditingSong(player.getName())).upNote(raycastPos.get(0), raycastPos.get(1));
            }
        } else if (slot == 5) {
            List<Integer> raycastPos = NoteblockUtil.getRaycastedInteractionPos(player);
            if (raycastPos != null) {
                plugin.getSheetMusic(plugin.getEditingSong(player.getName())).downNote(raycastPos.get(0), raycastPos.get(1));
            }
        }

        e.setCancelled(true);

    }

}
