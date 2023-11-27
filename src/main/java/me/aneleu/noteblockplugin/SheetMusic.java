package me.aneleu.noteblockplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SheetMusic {

    static final Transformation VERTICAL_LINE_TRANSFORMATION = new Transformation(new Vector3f(-0.5F, 0.001F, -0.5F), new AxisAngle4f(), new Vector3f(0.02F, 0, 1), new AxisAngle4f());
    static final Transformation HORIZONTAL_LINE_TRANSFORMATION = new Transformation(new Vector3f(-0.5F, 0.001F, -0.51F), new AxisAngle4f(), new Vector3f(0.02F, 0, 0.99F), new AxisAngle4f());
    static final Transformation TEXT_TRANSFORMATION = new Transformation(new Vector3f(0.48F, -0.92F, 0.001F), new AxisAngle4f(), new Vector3f(1.5F, 1.5F, 1), new AxisAngle4f());
    static final BlockData BLACK_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.BLACK_CONCRETE);
    static final BlockData GRAY_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.GRAY_CONCRETE);
    static final BlockData LIGHT_GRAY_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.LIGHT_GRAY_CONCRETE);

    static final int INITIAL_LENGTH = 64;
    static final int INITIAL_LINE = 10;
    static final int LENGTH_EXTEND = 32;
    static final int LINE_EXTEND = 3;
    static final int LENGTH_EXTEND_TRIGGER = 32;
    static final int LINE_EXTEND_TRIGGER = 2;
    static final int LENGTH_COLLAPSE_TRIGGER = 64;
    static final int LINE_COLLAPSE_TRIGGER = 5;
    static final int LENGTH_COLLAPSE_LIMIT = 32;
    static final int LINE_COLLAPSE_LIMIT = 3;

    NoteblockPlugin plugin;

    String name;
    int x;
    int y;
    int z;
    int length;
    int line;

    World world;
    Location location;

    public SheetMusic(String name, int x, int y, int z) {
        this(name, x, y, z, 1, 1);
        createBox(0, 0);
        extendLength(INITIAL_LENGTH - 1);
        extendLine(INITIAL_LINE - 1);
    }

    public SheetMusic(String name, int x, int y, int z, int length, int line) {
        this.plugin = NoteblockPlugin.plugin;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = length;
        this.line = line;
        this.world = Bukkit.getWorld("world");
        this.location = new Location(world, 0, this.y, 0);
    }

    private void addEntityUUID(@NotNull Entity entity, String tag) {
        addEntityUUID(entity.getUniqueId().toString(), tag);
    }

    private void addEntityUUID(String uuid, String tag) {
        plugin.getConfig().set("sheet." + name + ".entity." + tag, uuid);
    }

    private void removeUniqueIdEntity(String uuid) {
        if (uuid == null) return;
        Entity entity = Bukkit.getEntity(UUID.fromString(uuid));
        if (entity == null) return;
        entity.remove();
    }

    private void createBox(int a, int b) {
        location.set(a + x + 0.5, y, b + z + 0.5);

        world.getBlockAt(location).setType(Material.WHITE_CONCRETE);

        Interaction interaction = (Interaction) world.spawnEntity(location, EntityType.INTERACTION);
        String interactionUUID = interaction.getUniqueId().toString();
        plugin.getConfig().set("sheet." + name + ".interaction." + interactionUUID, List.of(a, b));

        addEntityUUID(interactionUUID, a + "|" + b + "|" + "interaction");

        location.add(0, 1, 0);
        for (int i = 0; i < 2; i++) {
            BlockDisplay outline = (BlockDisplay) world.spawnEntity(location, EntityType.BLOCK_DISPLAY);
            Location outline_loc = outline.getLocation();
            outline_loc.setYaw(90 * i);
            outline.teleport(outline_loc);
            if (i == 0) {
                if (a % 16 == 0) {
                    outline.setBlock(BLACK_CONCRETE_BLOCKDATA);
                } else if (a % 4 == 0) {
                    outline.setBlock(GRAY_CONCRETE_BLOCKDATA);
                } else {
                    outline.setBlock(LIGHT_GRAY_CONCRETE_BLOCKDATA);
                }
                outline.setTransformation(VERTICAL_LINE_TRANSFORMATION);
            } else {
                outline.setBlock(LIGHT_GRAY_CONCRETE_BLOCKDATA);
                outline.setTransformation(HORIZONTAL_LINE_TRANSFORMATION);
            }

            addEntityUUID(outline, a + "|" + b + "|" + "outline" + i);
        }

    }


    private void extendLength(int n) {
        int start = length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < line; j++) {
                createBox(start + i, j);
            }
        }
        length += n;
        plugin.getConfig().set("sheet." + name + ".length", length);
    }

    private void extendLine(int n) {
        int start = line;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < length; j++) {
                createBox(j, start + i);
            }
        }
        line += n;
        plugin.getConfig().set("sheet." + name + ".line", line);
    }

    private void removeBox(int a, int b) {
        world.getBlockAt(x + a, y, z + b).setType(Material.AIR);
        String interactionPath = "sheet." + name + ".entity." + a + "|" + b + "|interaction";
        String outline0Path = "sheet." + name + ".entity." + a + "|" + b + "|outline0";
        String outline1Path = "sheet." + name + ".entity." + a + "|" + b + "|outline1";
        String interactionUUID = plugin.getConfig().getString(interactionPath);
        String outlineUUID0 = plugin.getConfig().getString(outline0Path);
        String outlineUUID1 = plugin.getConfig().getString(outline1Path);
        plugin.getConfig().set("sheet." + name + ".interaction." + interactionUUID, null);
        plugin.getConfig().set(interactionPath, null);
        plugin.getConfig().set(outline0Path, null);
        plugin.getConfig().set(outline1Path, null);
        removeUniqueIdEntity(interactionUUID);
        removeUniqueIdEntity(outlineUUID0);
        removeUniqueIdEntity(outlineUUID1);
    }

    private void collapseLength(int n) {
        for (int i = length - n; i < length; i++) {
            for (int j = 0; j < line; j++) {
                removeBox(i, j);
            }
        }
        length -= n;
    }

    private void collapseLine(int n) {
        for (int j = line - n; j < line; j++) {
            for (int i = 0; i < length; i++) {
                removeBox(i, j);
            }
        }
        line -= n;
    }

    public void setNote(int a, int b, NoteblockNote note) {

        // 에디터 연장
        if (a >= length - LENGTH_EXTEND_TRIGGER) {
            extendLength(LENGTH_EXTEND);
        }
        if (b >= line - LINE_EXTEND_TRIGGER) {
            extendLine(LINE_EXTEND);
        }

        world.getBlockAt(x + a, y, z + b).setType(note.getInstrumentBlock());

        String displayUUID = plugin.getConfig().getString("sheet." + name + ".note." + a + "." + b + ".display");
        if (displayUUID != null) {
            Entity entity = Bukkit.getEntity(UUID.fromString(displayUUID));
            if (entity != null) {
                entity.remove();
            }
        }

        location.set(x + a, y + 1, z + b);
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);
        Component component = Component.text(note.getOctave() + " ", note.getOctaveColor())
                .append(Component.text(note.getNoteSymbol() + "\n", note.getNoteColor()))
                .append(Component.text(note.getVolume(), note.getVolumeColor()));
        textDisplay.text(component);
        textDisplay.setRotation(0, -90);
        textDisplay.setTransformation(TEXT_TRANSFORMATION);
        textDisplay.setDefaultBackground(false);
        textDisplay.setBackgroundColor(Color.fromARGB(0));

        plugin.getConfig().set("sheet." + name + ".note." + a + "." + b + ".display", textDisplay.getUniqueId().toString());
        plugin.getConfig().set("sheet." + name + ".note." + a + "." + b + ".note", note);

    }


    public void deleteNote(int a, int b, boolean reduce) { // reduce: [에디터를 축소 할수 있다면 축소 시키는 작업]을 수행할 것인지.
        String displayUUID = plugin.getConfig().getString("sheet." + name + ".note." + a + "." + b + ".display");
        if (displayUUID != null) {
            Entity entity = Bukkit.getEntity(UUID.fromString(displayUUID));
            if (entity != null) {
                entity.remove();
            }

            plugin.getConfig().set("sheet." + name + ".note." + a + "." + b, null);
            world.getBlockAt(x + a, y, z + b).setType(Material.WHITE_CONCRETE);

            if (reduce) reduce();
        }

    }

    public void reduce() {

        int maxLength = 0;
        int maxLine = 0;

        ConfigurationSection textDisplaySection1 = plugin.getConfig().getConfigurationSection("sheet." + name + ".note");
        if (textDisplaySection1 == null) {
            return;
        }
        for (String i : textDisplaySection1.getKeys(false)) {
            ConfigurationSection textDisplaySection2 = textDisplaySection1.getConfigurationSection(i);
            if (textDisplaySection2 == null) {
                continue;
            }
            Set<String> keys = textDisplaySection2.getKeys(false);
            if (keys.isEmpty()) {
                continue;
            }
            int integerI = Integer.parseInt(i);
            if (integerI > maxLength) {
                maxLength = integerI;
            }
            for (String j : textDisplaySection2.getKeys(false)) {
                int integerJ = Integer.parseInt(j);
                if (integerJ > maxLine) {
                    maxLine = integerJ;
                }
            }
        }

        if (length - maxLength > LENGTH_COLLAPSE_TRIGGER) {
            if (maxLength + 1 + LENGTH_COLLAPSE_LIMIT >= INITIAL_LENGTH) {
                collapseLength(length - maxLength - 1 - LENGTH_COLLAPSE_LIMIT);
            } else {
                collapseLength(length - INITIAL_LENGTH);
            }
        }
        if (line - maxLine > LINE_COLLAPSE_TRIGGER) {
            if (maxLine + 1 + LINE_COLLAPSE_LIMIT >= INITIAL_LINE) {
                collapseLine(line - maxLine - 1 - LINE_COLLAPSE_LIMIT);
            } else  {
                collapseLine(line - INITIAL_LINE);
            }

        }

    }

    public void remove() {
        ConfigurationSection entitySection = plugin.getConfig().getConfigurationSection("sheet." + name + ".entity");
        if (entitySection != null) {
            for (String i : entitySection.getKeys(false)) {
                Entity entity = Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(entitySection.getString(i))));
                if (entity != null) {
                    entity.remove();
                }
            }
        }

        removeText:
        {
            ConfigurationSection textDisplaySection1 = plugin.getConfig().getConfigurationSection("sheet." + name + ".note");
            if (textDisplaySection1 == null) {
                break removeText;
            }
            for (String i : textDisplaySection1.getKeys(false)) {
                ConfigurationSection textDisplaySection2 = textDisplaySection1.getConfigurationSection(i);
                if (textDisplaySection2 == null) {
                    continue;
                }
                for (String j : textDisplaySection2.getKeys(false)) {
                    String displayUUID = textDisplaySection2.getString(j + ".display");
                    if (displayUUID == null) {
                        continue;
                    }
                    Entity entity = Bukkit.getEntity(UUID.fromString(displayUUID));
                    if (entity != null) {
                        entity.remove();
                    }
                }
            }
        }

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < line; j++) {
                location.set(x + i, y, z + j);
                world.getBlockAt(location).setType(Material.AIR);
            }
        }

        plugin.getConfig().set("sheet." + name, null);

    }

}
