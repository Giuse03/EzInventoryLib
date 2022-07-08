package eu.giuse.inventorylib;

import eu.giuse.inventorylib.workloads.WorkloadCloseInv;
import eu.giuse.inventorylib.workloads.WorkloadOpenInv;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.giuse.engine.Worker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class InventoryBuilder implements Listener {
    public final JavaPlugin javaPlugin;
    private final int rows;
    @Getter
    private final String name;
    @Getter
    private final HashMap<Integer, Inventory> inventoryHash = new HashMap<>();

    @Getter
    private final ArrayList<ButtonBuilder> buttonBuilders = new ArrayList<>();
    @Getter private final int nPage;

    @Setter private boolean openInvAsync, closeInvAsync;
    @Setter
    private WorkloadOpenInv workloadOpenInv;
    @Setter
    private WorkloadCloseInv workloadCloseInv;
    private final HashMap<UUID, Integer> pageCounter = new HashMap<>();

    public InventoryBuilder createInvs() {
        for (int i = 1; i < nPage + 1; i++) {
            inventoryHash.put(i, Bukkit.createInventory(null, 9 * rows, name.replace("%page%", String.valueOf(i))));
        }
        return this;
    }

    public void nextPage(Player player) {
        if (pageCounter.get(player.getUniqueId()) == nPage) {
            pageCounter.replace(player.getUniqueId(), 1);
            player.closeInventory();
            player.openInventory(inventoryHash.get(pageCounter.get(player.getUniqueId())));
            return;
        }
        pageCounter.replace(player.getUniqueId(), pageCounter.get(player.getUniqueId()) + 1);
        player.closeInventory();
        player.openInventory(inventoryHash.get(pageCounter.get(player.getUniqueId())));
    }

    public void previousPage(Player player) {
        if (pageCounter.get(player.getUniqueId()) == 1) {
            pageCounter.replace(player.getUniqueId(), nPage);
            player.closeInventory();
            player.openInventory(inventoryHash.get(pageCounter.get(player.getUniqueId())));
            return;
        }
        pageCounter.replace(player.getUniqueId(), pageCounter.get(player.getUniqueId()) - 1);
        player.closeInventory();
        player.openInventory(inventoryHash.get(pageCounter.get(player.getUniqueId())));
    }


    public InventoryBuilder addButton(ButtonBuilder buttonBuilder) {
        buttonBuilders.add(buttonBuilder);
        return this;
    }

    public void build() {
        for (ButtonBuilder buttonBuilder : buttonBuilders) {
            for (Integer value : inventoryHash.keySet()) {
                if (buttonBuilder.getPage() == value) {
                    inventoryHash.get(value).setItem(buttonBuilder.getPosition(), buttonBuilder.getItemStack());
                }
            }
            Bukkit.getPluginManager().registerEvents(buttonBuilder, javaPlugin);
        }
        Bukkit.getPluginManager().registerEvents(this, javaPlugin);

    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (e.getView().getTitle().contains(name.replace("%page%",""))) {
            if (!pageCounter.containsKey(e.getPlayer().getUniqueId())) pageCounter.put(e.getPlayer().getUniqueId(), 1);
            if (workloadOpenInv != null) Worker.executeProcess(CompletableFuture.supplyAsync(() -> () -> workloadOpenInv.compute(e)), true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().contains(name.replace("%page%",""))) {
            if (workloadCloseInv != null)  Worker.executeProcess(CompletableFuture.supplyAsync(() -> () -> workloadCloseInv.compute(e)), true);
        }
    }
}
