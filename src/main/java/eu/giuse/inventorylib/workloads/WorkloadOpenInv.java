package eu.giuse.inventorylib.workloads;

import org.bukkit.event.inventory.InventoryOpenEvent;

public interface WorkloadOpenInv {
    void compute(InventoryOpenEvent event);
}
