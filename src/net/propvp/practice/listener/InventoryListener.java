package net.propvp.practice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.propvp.practice.utils.InventoryFactory;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInvInteract(InventoryClickEvent event) {
		if(InventoryFactory.opened.contains(event.getWhoClicked().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent event) {
		if(InventoryFactory.opened.contains(event.getPlayer().getUniqueId())) {
			InventoryFactory.opened.remove(event.getPlayer().getUniqueId());
		}
	}
	
}