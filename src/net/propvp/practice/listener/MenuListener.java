package net.propvp.practice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.propvp.practice.menu.EditorMenu;
import net.propvp.practice.menu.NormalMenu;
import net.propvp.practice.menu.PartyMenu;
import net.propvp.practice.menu.PlayerOptions;
import net.propvp.practice.menu.RankedMenu;
import net.propvp.practice.utils.ItemFactory;

public class MenuListener implements Listener {
	
	@EventHandler
	public void onMenuOpen(PlayerInteractEvent e) {
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) return;
		if(e.getItem() == null) return;
		if(e.getItem().equals(ItemFactory.getRankedMatchFinder())) { RankedMenu.openMenu(e.getPlayer()); e.setCancelled(true); }
		if(e.getItem().equals(ItemFactory.getNormalMatchFinder())) { NormalMenu.openMenu(e.getPlayer()); e.setCancelled(true); }
		if(e.getItem().equals(ItemFactory.getPartyMatchFinder())) { PartyMenu.openMenu(e.getPlayer()); e.setCancelled(true); }
		if(e.getItem().equals(ItemFactory.getKitEditor())) { EditorMenu.openMenu(e.getPlayer()); e.setCancelled(true); }
		if(e.getItem().equals(ItemFactory.getSpectatorOptions())) { PlayerOptions.openMenu(e.getPlayer()); e.setCancelled(true); }
	}
	
}