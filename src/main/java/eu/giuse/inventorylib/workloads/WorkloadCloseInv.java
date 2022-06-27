package eu.giuse.inventorylib.workloads;

import org.bukkit.event.inventory.InventoryCloseEvent;

public interface WorkloadCloseInv {
    void compute(InventoryCloseEvent event);
}
