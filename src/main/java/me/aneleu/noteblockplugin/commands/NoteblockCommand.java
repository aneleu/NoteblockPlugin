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
/noteblock reduce : reduce sheet
 */

public class NoteblockCommand implements TabExecutor {

    private final NoteblockPlugin plugin;
    private final List<String> mainArgList = List.of("create", "remove", "generate", "edit", "save");
    private final List<String> editArgList = List.of("stop", "start", "note", "volume", "copy", "paste", "undo", "redo", "clipboard");

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
                    return true;

                }

                if (plugin.getEditingSong(p.getName()) == null) {
                    p.sendMessage(NOT_EDITING);
                    return true;
                }

                if (args[1].equalsIgnoreCase("stop")) {

                    NoteblockUtil.stopEditing(p);

                } else if (args[1].equalsIgnoreCase("note")) {
                    NoteblockUtil.setPlayerNote(
                            p.getName(),
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3])
                    );

                } else if (args[1].equalsIgnoreCase("volume")) {

                    NoteblockUtil.setPlayerVolume(
                            p.getName(),
                            Integer.parseInt(args[2])
                    );

                } else if (args[1].equalsIgnoreCase("copy")) {

                    plugin.getSheetMusic(plugin.getEditingSong(p.getName())).copy(
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3]),
                            Integer.parseInt(args[4]),
                            Integer.parseInt(args[5])
                    );

                } else if (args[1].equalsIgnoreCase("paste")) {

                    plugin.getSheetMusic(plugin.getEditingSong(p.getName())).paste(
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3])
                    );

                } else if (args[1].equalsIgnoreCase("undo")) {

                    plugin.getSheetMusic(plugin.getEditingSong(p.getName())).undo();

                } else if (args[1].equalsIgnoreCase("redo")) {

                    plugin.getSheetMusic(plugin.getEditingSong(p.getName())).redo();

                } else if (args[1].equalsIgnoreCase("clipboard")) {

                    if (args[2].equalsIgnoreCase("delete")) {
                        plugin.getSheetMusic(plugin.getEditingSong(p.getName())).deleteClipboard(args[3]);
                    } else if (args[2].equalsIgnoreCase("save")) {
                        plugin.getSheetMusic(plugin.getEditingSong(p.getName())).saveClipboard(args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7]));
                    } else if (args[2].equalsIgnoreCase("load")) {
                        plugin.getSheetMusic(plugin.getEditingSong(p.getName())).loadClipboard(args[3]);
                    } else if (args[2].equalsIgnoreCase("list")) {
                        List<String> clipboardList = plugin.getSheetMusic(plugin.getEditingSong(p.getName())).getClipboardList();
                        if (clipboardList.isEmpty()) {
                            p.sendMessage(Component.text("Clipboard is Empty", NamedTextColor.RED));
                            return true;
                        }
                        p.sendMessage(Component.text("\n---- Clipboard list ----", NamedTextColor.AQUA));
                        for (String clipboard : clipboardList) {
                            p.sendMessage(Component.text(clipboard, NamedTextColor.GREEN));
                        }

                    } else {
                        p.sendMessage(SUGGESTION_EDIT);
                    }

                } else {
                    p.sendMessage(SUGGESTION_EDIT);
                }
            } else if (args[0].equalsIgnoreCase("save")) {
                plugin.saveConfig();
            } else if (args[0].equalsIgnoreCase("test")) {
                if (args[1].equalsIgnoreCase("undo")) {
                    plugin.getSheetMusic(plugin.getEditingSong(p.getName())).undo();
                    return true;
                }
                if (args[1].equalsIgnoreCase("redo")) {
                    plugin.getSheetMusic(plugin.getEditingSong(p.getName())).redo();
                    return true;
                }
                if (args[1].equalsIgnoreCase("copy")) {
                    plugin.getSheetMusic(plugin.getEditingSong(p.getName())).copy(
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3]),
                            Integer.parseInt(args[4]),
                            Integer.parseInt(args[5])
                    );
                    return true;
                }
                if (args[1].equalsIgnoreCase("paste")) {
                    plugin.getSheetMusic(plugin.getEditingSong(p.getName())).paste(
                            Integer.parseInt(args[2]),
                            Integer.parseInt(args[3])
                    );
                    return true;
                }
                NoteblockUtil.setPlayerNote(p.getName(), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
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
            return mainArgList;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("generate")) {
                return plugin.getConfig().getStringList("list");
            } else if (args[0].equalsIgnoreCase("edit")) {
                return editArgList;
            }

        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("start")) {
                return plugin.getConfig().getStringList("list");
            }
        }

        return List.of();
    }
}
