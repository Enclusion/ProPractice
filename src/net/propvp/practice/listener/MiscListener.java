package net.propvp.practice.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.propvp.practice.Practice;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.ItemFactory;

public class MiscListener implements Listener {
	
	@EventHandler
	public void onPlayerHider(PlayerInteractEvent e) {
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) return;
		if(e.getItem() == null) return;
		if(e.getItem().equals(ItemFactory.getPlayerHider(e.getPlayer()))) {
			Player player = e.getPlayer();
			PlayerData data = Practice.getInstance().getDataManager().getData(player);
			
			if(data.isHidingPlayers()) {
				Practice.getInstance().getEntityHider().showAllPlayers(player);
				data.setHidingPlayers(false);
				player.sendMessage(ChatColor.GRAY + "You are now showing players.");
			} else {
				Practice.getInstance().getEntityHider().hideAllPlayers(player);
				data.setHidingPlayers(true);
				player.sendMessage(ChatColor.GRAY + "You are now hiding players.");
			}
			
			e.getPlayer().setItemInHand(ItemFactory.getPlayerHider(e.getPlayer()));
			e.getPlayer().updateInventory();
		}
	}
	
	@EventHandler
	public void onLeaveQueue(PlayerInteractEvent e) {
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) return;
		if(e.getItem() == null) return;
		if(e.getItem().equals(ItemFactory.getLeaveQueue())) {
			Player player = e.getPlayer();
			PlayerData data = Practice.getInstance().getDataManager().getData(player);
			
			if(data.inMatchmaking()) {
				if(data.inParty()) {
					data.getMatchMaker().removeObject(data.getParty());
				} else {
					data.getMatchMaker().removeObject(player);
				}
			}
		}
	}

}