package eu.giuse.inventorylib;

import eu.giuse.inventorylib.workloads.WorkloadClickEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.giuse.engine.Worker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

@Getter @RequiredArgsConstructor
public class ButtonBuilder implements Listener {
    private final InventoryBuilder inventoryBuilder;
    private final int position;
    private final int page;

    private final ItemStack itemStack;
    @Setter private WorkloadClickEvent event;
    @Setter private boolean async;
    private final boolean nextPage,previousPage,eventCancelled;
    private final Worker worker;
    @EventHandler
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getSlot() == position && inventoryBuilder.getInventoryHash().get(page).equals(inventoryClickEvent.getInventory())) {
            if (nextPage && inventoryBuilder.getNPage() > 1) inventoryBuilder.nextPage((Player) inventoryClickEvent.getWhoClicked());
            if (previousPage&& inventoryBuilder.getNPage() >1) inventoryBuilder.previousPage((Player) inventoryClickEvent.getWhoClicked());
            if(eventCancelled) inventoryClickEvent.setCancelled(true);
            if(event != null) worker.executeProcess(CompletableFuture.supplyAsync(() -> () -> event.compute(inventoryClickEvent)), async);
        }
    }
}
