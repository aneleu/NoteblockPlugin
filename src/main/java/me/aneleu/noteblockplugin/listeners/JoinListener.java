package me.aneleu.noteblockplugin.listeners;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import me.aneleu.noteblockplugin.utils.NoteblockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    NoteblockPlugin plugin;
    public JoinListener(NoteblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String song = plugin.getConfig().getString("player." + p.getName() + ".song");
        if (song != null) {
            NoteblockUtil.startEditing(p, song);
        } else {
            plugin.removeEditingPlayer(p.getName());
        }

    }

}
