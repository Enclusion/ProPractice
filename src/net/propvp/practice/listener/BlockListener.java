package net.propvp.practice.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import net.propvp.practice.Practice;
import net.propvp.practice.player.PlayerData;

public class BlockListener implements Listener {

	private Practice plugin;

	public BlockListener(Practice plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.getDataManager().getData(player);

		if(player.hasPermission("propractice.bypass") || player.isOp()) return;
		
		if(!data.inMatch()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.getDataManager().getData(player);

		if(player.hasPermission("propractice.bypass") || player.isOp()) return;
		
		if(!data.inMatch()) {
			event.setCancelled(true);
		}
	}
	
}