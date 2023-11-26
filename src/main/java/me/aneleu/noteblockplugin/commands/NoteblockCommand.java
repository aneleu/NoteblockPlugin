package me.aneleu.noteblockplugin.commands;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import me.aneleu.noteblockplugin.SheetMusic;
import me.aneleu.noteblockplugin.utils.NoteblockUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static me.aneleu.noteblockplugin.utils.Messages.*;

/*
/noteblock create <name> : create sheet music
/noteblock remove <name> : delete sheet music
/noteblock generate <name> : generate noteblock
/noteblock edit start : edit tool enable
/noteblock edit stop : edit tool disable
/noteblock save : save config
/noteblock reduce length <num> : reduce length
/noteblock reduce line <num> : reduce line
 */

public class NoteblockCommand implements TabExecutor {

    private final NoteblockPlugin plugin;
    private final List<String> arg1_list = List.of("create", "remove", "generate", "edit", "save", "reduce");
    private final List<String> edit_list = List.of("stop", "start");

    private final List<String> reduce_list = List.of("line", "length");

    public NoteblockCommand() {
        this.plugin = NoteblockPlugin.plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p) {

            if (args[0].equalsIgnoreCase("create")) {

                List<String> list = plugin.getConfig().getStringList("list");
                if (list.contains(args[1])) {
                    p.sendMessage(SONG_ALREADY_EXIST);
                } else {
                    list.add(args[1]);
                    plugin.getConfig().set("list", list);
                    plugin.getConfig().set("sheet." + args[1] + ".x", p.getLocation().getBlockX());
                    plugin.getConfig().set("sheet." + args[1] + ".y", p.getLocation().getBlockY());
                    plugin.getConfig().set("sheet." + args[1] + ".z", p.getLocation().getBlockZ());
                    SheetMusic sheetMusic = new SheetMusic(args[1], p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
                    plugin.addSheetMusic(args[1], sheetMusic);
                }

            } else if (args[0].equalsIgnoreCase("remove")) {

                List<String> list = plugin.getConfig().getStringList("list");
                if (!(list.contains(args[1]))) {
                    p.sendMessage(SONG_NONEXIST);
                } else {
                    list.remove(args[1]);
                    plugin.getConfig().set("list", list);
                    plugin.getSheetMusic(args[1]).remove();
                    plugin.removeSheetMusic(args[1]);
                }

                ConfigurationSection player_section = plugin.getConfig().getConfigurationSection("player");
                if (player_section != null) {
                    Set<String> players = player_section.getKeys(false);
                    for (String player : players) {
                        if (plugin.getConfig().getString("player." + player + ".song").equalsIgnoreCase(args[1])) {
                            plugin.getConfig().set("player." + player, null);
                            Player onlinePlayer = Bukkit.getPlayer(player);
                            if (onlinePlayer != null) {
                                NoteblockUtil.stopEditing(onlinePlayer);
                            }
                        }
                    }

                }

            } else if (args[0].equalsIgnoreCase("generate")) {
                // TODO
            } else if (args[0].equalsIgnoreCase("edit")) {
                if (args[1].equalsIgnoreCase("start")) {

                    NoteblockUtil.startEditing(p, args[2]);

                } else if (args[1].equalsIgnoreCase("stop")) {

                    NoteblockUtil.stopEditing(p);

                } else {
                    p.sendMessage(SUGGESTION_EDIT);
                }
            } else if (args[0].equalsIgnoreCase("save")) {
                plugin.saveConfig();
            } else if (args[0].equalsIgnoreCase("reduce")) {
                SheetMusic sheetMusic = plugin.getSheetMusic(plugin.getEditingSong(p.getName()));
                if (sheetMusic != null) {
                    if (args[1].equalsIgnoreCase("line")) {
                        sheetMusic.reduceLine();
                    } else if (args[1].equalsIgnoreCase("length")) {
                        sheetMusic.reduceLength();
                    } else {
                        p.sendMessage(SUGGESTION_REDUCE);
                    }
                } else {
                    p.sendMessage(Component.text());
                }

            } else {
                p.sendMessage(SUGGESTION_MAIN);
            }

        } else {
            Bukkit.broadcast(ONLY_PLAYER);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return arg1_list;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("generate")) {
                return plugin.getConfig().getStringList("list");
            } else if (args[0].equalsIgnoreCase("edit")) {
                return edit_list;
            } else if (args[0].equalsIgnoreCase("reduce")) {
                return reduce_list;
            }

        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("start")) {
                return plugin.getConfig().getStringList("list");
            }
        }

        return List.of();
    }
}
