package me.aneleu.noteblockplugin.tasks;

import me.aneleu.noteblockplugin.NoteblockPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EditTask extends BukkitRunnable {

    // TODO EditTask에서 EditListener로 바꾸면 삭제하기

    private final Player player;
    public EditTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {

        int slot = player.getInventory().getHeldItemSlot();
        switch (slot) {
            case 0:
                System.out.println(1);
        }


        player.getInventory().setHeldItemSlot(4);
    }
}
