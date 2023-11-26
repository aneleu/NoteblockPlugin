package me.aneleu.noteblockplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class SheetMusic {

    static final Transformation VERTICAL_LINE_TRANSFORMATION = new Transformation(new Vector3f(-0.5F, 0.001F, -0.5F), new AxisAngle4f(), new Vector3f(0.02F, 0, 1), new AxisAngle4f());
    static final Transformation HORIZONTAL_LINE_TRANSFORMATION = new Transformation(new Vector3f(-0.5F, 0.001F, -0.51F), new AxisAngle4f(), new Vector3f(0.02F, 0, 0.99F), new AxisAngle4f());
    static final Transformation TEXT_TRANSFORMATION = new Transformation(new Vector3f(0.48F, -0.9F, 1.001F), new AxisAngle4f(), new Vector3f(1.5F,1.5F,1), new AxisAngle4f());
    static final BlockData BLACK_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.BLACK_CONCRETE);
    static final BlockData GRAY_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.GRAY_CONCRETE);
    static final BlockData LIGHT_GRAY_CONCRETE_BLOCKDATA = Bukkit.createBlockData(Material.LIGHT_GRAY_CONCRETE);

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
        makeBox(0, 0);
        addLength(63);
        addLine(9);
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

    private void addEntityUUID(String uuid) {
        int count = plugin.getConfig().getInt("sheet." + name + ".entitycount");
        plugin.getConfig().set("sheet." + name + ".entity." + count, uuid);
        plugin.getConfig().set("sheet." + name + ".entitycount", ++count);
    }

    private void makeBox(int a, int b) {
        location.set(a + x + 0.5, y, b + z + 0.5);

        world.getBlockAt(location).setType(Material.WHITE_CONCRETE);

        Interaction interaction = (Interaction) world.spawnEntity(location, EntityType.INTERACTION);
        String interactionUUID = interaction.getUniqueId().toString();
        plugin.getConfig().set("sheet." + name + ".interaction." + interactionUUID, List.of(a, b));

        List<String> entityList = plugin.getConfig().getStringList("sheet." + name + ".entity");
        entityList.add(interactionUUID);

        location.add(0, 1, 0);
        for (int i = 0; i < 4; i++) {
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
            } else if (i == 1) {
                outline.setBlock(LIGHT_GRAY_CONCRETE_BLOCKDATA);
                outline.setTransformation(HORIZONTAL_LINE_TRANSFORMATION);
            }

            entityList.add(outline.getUniqueId().toString());
        }

        plugin.getConfig().set("sheet." + name + ".entity", entityList);


    }

    private void addLength(int n) {
        int start = length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < line; j++) {
                makeBox(start + i, j);
            }
        }
        length += n;
        plugin.getConfig().set("sheet." + name + ".length", length);
    }

    private void addLine(int n) {
        int start = line;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < length; j++) {
                makeBox(j, start + i);
            }
        }
        line += n;
        plugin.getConfig().set("sheet." + name + ".line", line);
    }

    public void setNote(int a, int b, NoteblockNote note) {

        // 에디터 연장
        if (a >= length - 32) {
            addLength(32);
        }
        if (b >= line - 2) {
            addLine(3);
        }

        plugin.getConfig().set("sheet." + name + ".note." + a + "." + b + ".note", note);

        // TODO 악기 및 옥타브에 따라 텍스트이 색 변하게... / 악기 -> 블럭 다르게
        world.getBlockAt(x + a, y, z + b).setType(Material.STONE);
        location.set(x + a, y, z + b);
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);
        Component component = Component.text(note.getOctave() + " ", note.getOctaveColor())
                .append(Component.text(note.getNoteSymbol() + "\n", note.getNoteColor()))
                .append(Component.text(note.getVolume(), note.getVolumeColor()));
        textDisplay.text(component);
        textDisplay.setRotation(0, -90);
        textDisplay.setTransformation(TEXT_TRANSFORMATION);
        textDisplay.setDefaultBackground(false);
        textDisplay.setBackgroundColor(Color.fromARGB(0));

        plugin.saveConfig();

    }

    public void deleteNote(int a, int b) {
        plugin.getConfig().set("sheet." + name + ".note." + a + "." + b, null);
        world.getBlockAt(x + a, y, z + b).setType(Material.WHITE_CONCRETE);

        plugin.saveConfig();

    }

    public void reduceLine() {
        // TODO 적당히 감소
    }

    public void reduceLength() {
        // TODO
    }

    public void remove() {
        List<String> entitiesUUID = plugin.getConfig().getStringList("sheet." + name + ".entity");
        for (String entityUUID : entitiesUUID) {
            Entity entity = Bukkit.getEntity(UUID.fromString(entityUUID));
            if (entity != null) {
                entity.remove();
            }
        }

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < line; j++) {
                location.set(x + i, y, z + j);
                world.getBlockAt(location).setType(Material.AIR);
            }
        }

        plugin.getConfig().set("sheet." + name, null);

        plugin.saveConfig();

    }

}
