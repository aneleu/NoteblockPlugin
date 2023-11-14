package me.aneleu.noteblockplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class SheetMusic {

    static final Transformation trans_outline = new Transformation(new Vector3f(-0.5F, 1.001F, -0.5F), new AxisAngle4f(), new Vector3f(0.02F, 0, 1), new AxisAngle4f());

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

    private void makeBox(double a, double b) {
        location.setX(a+x+0.5);
        location.setZ(b+z+0.5);

        world.getBlockAt(location).setType(Material.WHITE_CONCRETE);

        Interaction interaction = (Interaction) world.spawnEntity(location, EntityType.INTERACTION);
        String interactionUUID = interaction.getUniqueId().toString();
        plugin.getConfig().set("sheet." + name + ".interaction."+interactionUUID, List.of(a, b));

        List<String> entity_list = plugin.getConfig().getStringList("sheet." + name + ".entity");
        entity_list.add(interactionUUID);


        for (int i = 0; i < 4; i++) {
            BlockDisplay outline = (BlockDisplay) world.spawnEntity(location, EntityType.BLOCK_DISPLAY);
            outline.setBlock(Material.BLACK_CONCRETE.createBlockData());
            outline.setTransformation(trans_outline);
            Location outline_loc = outline.getLocation();
            outline_loc.setYaw(90 * i);
            outline.teleport(outline_loc);
            entity_list.add(outline.getUniqueId().toString());
        }

        plugin.getConfig().set("sheet." + name + ".entity", entity_list);


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
        plugin.saveConfig();
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
        plugin.saveConfig();
    }

    public void remove() {
        List<String> entitiesUUID = plugin.getConfig().getStringList("sheet." + name + ".entity");
        for (String entityUUID: entitiesUUID) {
            Entity entity = Bukkit.getEntity(UUID.fromString(entityUUID));
            if (entity != null) {
                entity.remove();
            }
        }

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < line; j++) {
                location.setX(x+i);
                location.setZ(z+j);
                world.getBlockAt(location).setType(Material.AIR);
            }
        }

        plugin.getConfig().set("sheet." + name, null);
        plugin.saveConfig();

    }

}
