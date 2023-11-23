package me.aneleu.noteblockplugin.listeners;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import me.aneleu.noteblockplugin.utils.NoteblockUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    // TODO Task에서 Listener로 바꾸면 이거 필요없어짐. 그때 삭제하기.

    NoteblockPlugin plugin;
    public JoinListener(NoteblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String editingSong = plugin.getConfig().getString("player." + player.getName() + ".song");
        if (editingSong == null) {
            return;
        }

        NoteblockUtil.startTask(player, editingSong);

    }

}
