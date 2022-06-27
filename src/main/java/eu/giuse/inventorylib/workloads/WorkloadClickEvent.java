package eu.giuse.inventorylib.workloads;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface WorkloadClickEvent {
    void compute(InventoryClickEvent event);
}
