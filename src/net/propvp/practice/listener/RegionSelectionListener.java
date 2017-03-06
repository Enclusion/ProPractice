package net.propvp.practice.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.propvp.practice.game.arena.CuboidSelection;
import net.propvp.practice.utils.ItemFactory;

public class RegionSelectionListener implements Listener {

	@EventHandler
	public void onSelect(PlayerInteractEvent event) {
		if(event.getItem() == null) return;
		
		if(event.getAction() == Action.LEFT_CLICK_BLOCK && event.getItem().equals(ItemFactory.getRegionSelector())) {
			Block b = event.getClickedBlock();
			event.getPlayer().sendMessage(ChatColor.AQUA + "Selection point 1 set at " + b.getX() + ", " + b.getY() + ", " + b.getZ());
			CuboidSelection.setSelectionOne(event.getPlayer(), b.getLocation());
			event.setCancelled(true);
		}
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem().equals(ItemFactory.getRegionSelector())) {
			Block b = event.getClickedBlock();
			event.getPlayer().sendMessage(ChatColor.AQUA + "Selection point 2 set at " + b.getX() + ", " + b.getY() + ", " + b.getZ());
			CuboidSelection.setSelectionTwo(event.getPlayer(), b.getLocation());
			event.setCancelled(true);
		}
	}
	
}