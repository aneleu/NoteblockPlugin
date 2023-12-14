package me.aneleu.noteblockplugin.tasks;

import me.aneleu.noteblockplugin.Note;
import me.aneleu.noteblockplugin.NoteblockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class PlayTask extends BukkitRunnable {

    private static final NoteblockPlugin plugin = NoteblockPlugin.plugin;

    String songName;
    int time;
    int maxTime;
    int maxLine;
    Note[][] notes;
    List<Player> players;

    public PlayTask(String songName, int time) {

        this.songName = songName;
        this.time = time;
        maxTime = plugin.getConfig().getInt("sheet." + songName + ".length");
        maxLine = plugin.getConfig().getInt("sheet." + songName + ".line");
        notes = new Note[maxTime][maxLine];

        ConfigurationSection configurationSection1 = plugin.getConfig().getConfigurationSection("sheet." + songName + ".note");
        if (configurationSection1 != null) {
            for (String key1 : configurationSection1.getKeys(false)) {
                ConfigurationSection configurationSection2 = configurationSection1.getConfigurationSection(key1);
                if (configurationSection2 == null) {
                    continue;
                }
                for (String key2 : configurationSection2.getKeys(false)) {
                    Note note = configurationSection2.getSerializable(key2 + ".note", Note.class);
                    if (note != null) {
                        int x = Integer.parseInt(key1);
                        int y = Integer.parseInt(key2);
                        notes[x][y] = note;
                    }
                }
            }
        }

        players = plugin.getEditingPlayerList(songName).stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();

    }

    @Override
    public void run() {

        if (time >= maxTime) {
            cancel();
            return;
        }

        for (int i = 0; i < maxLine; i++) {
            Note note = notes[time][i];
            if (note != null) {
                for (Player p : players) {
                    note.playSound(p);
                }

            }
        }

        time++;

    }

}
