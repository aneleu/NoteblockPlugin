package me.aneleu.noteblockplugin.listeners;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
        String editingSong = plugin.getConfig().getString("player." + playerName);
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
        List<String> songList = plugin.getConfig().getStringList("list");

        String interactionSong = null;
        Integer interactionLength = null;
        Integer interactionLine = null;
        for (String song: songList) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("sheet."+song+".interaction.");
            if (section != null && section.contains(entityUUID)) {
                interactionSong = song;
                List<Integer> loc = plugin.getConfig().getIntegerList("sheet."+song+".interaction."+entityUUID);
                interactionLength = loc.get(0);
                interactionLine = loc.get(1);
                break;
            }
        }
        if (interactionSong == null || interactionLength == null || interactionLine == null) {
            return;
        }

        // 타일 감지 완료
        // TODO 감지한 타일에 노트 설치하고 콘피그에 노트 저장하기



    }

}
