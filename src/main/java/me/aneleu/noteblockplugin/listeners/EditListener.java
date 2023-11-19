package me.aneleu.noteblockplugin.listeners;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import me.aneleu.noteblockplugin.SheetMusic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import java.util.List;

public class EditListener implements Listener {

    private final NoteblockPlugin plugin;

    public EditListener(NoteblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        String playerName = player.getName();
        String editingSong = plugin.getConfig().getString("player." + playerName + ".song");
        if (editingSong == null) {
            return;
        }

        RayTraceResult rayTraceResult = player.rayTraceEntities(50, true);
        if (rayTraceResult == null) {
            return;
        }

        Entity entity = rayTraceResult.getHitEntity();
        if (entity == null || entity.getType() != EntityType.INTERACTION) {
            return;
        }

        String entityUUID = entity.getUniqueId().toString();

        List<Integer> pos = plugin.getConfig().getIntegerList("sheet." + editingSong + ".interaction." + entityUUID);

        if (pos.isEmpty()) {
            return;
        }

        int a = pos.get(0);
        int b = pos.get(1);
        String state = plugin.getConfig().getString("player." + playerName + ".state");
        SheetMusic sheetMusic = plugin.getSheetMusic(editingSong);

        if (state.equalsIgnoreCase("single")) {
            if (e.getAction().isLeftClick()) {
                String instrument = plugin.getConfig().getString("player." + playerName + ".instrument");
                int octave = plugin.getConfig().getInt("player." + playerName + ".octave");
                int note = plugin.getConfig().getInt("player." + playerName + ".note");
                int volume = plugin.getConfig().getInt("player." + playerName + ".volume");
                sheetMusic.setNote(a, b, instrument, octave, note, volume);
            } else if (e.getAction().isRightClick()) {
                sheetMusic.deleteNote(a, b);
            }

        } else if (state.equalsIgnoreCase("multi")) {

        } else if (state.equalsIgnoreCase("play_select")) {

        }

        e.setCancelled(true);

    }

}
