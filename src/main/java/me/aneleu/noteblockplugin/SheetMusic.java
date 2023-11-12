package me.aneleu.noteblockplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public class SheetMusic {

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
        makeBox(x, z);
        addLength(99);
        addLine(9);
    }

    public SheetMusic(String name, int x, int y, int z, int length, int line) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = length;
        this.line = line;
        this.world = Bukkit.getWorld("world");
        this.location = new Location(world, 0, this.y, 0);
    }

    public void makeBox(double x, double z) {
        location.setX(x);
        location.setZ(z);
        world.getBlockAt(location).setType(Material.WHITE_CONCRETE);

    }

    private void addLength(int n) {
        int start = x + length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < line; j++) {
                makeBox(start+i, z + j);
            }
        }
        length += n;
    }

    private void addLine(int n) {
        int start = z + line;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < length; j++) {
                makeBox(x + j, start + i);
            }
        }
        line += n;
    }

}
