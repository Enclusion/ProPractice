package net.propvp.practice.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.IconMenu;
import net.propvp.practice.utils.ItemBuilder;
import net.propvp.practice.utils.MessageUtils;

public class PlayerOptions {

	public static void openMenu(Player player) {
		PlayerData playerData = Practice.getInstance().getDataManager().getData(player);

		IconMenu menu = new IconMenu(MessageUtils.color(PracticeConfiguration.getRootConfig().getConfig().getString("menus.player-options")), 18, event -> {
			if(!event.getItemStack().getType().equals(Material.INK_SACK)) return;

			if(event.getPosition() == 10) {
				playerData.setHidingScoreboard(!playerData.isHidingScoreboard());
				event.getInventory().setItem(10, new ItemBuilder(Material.INK_SACK, (playerData.isHidingScoreboard() ? ChatColor.GRAY + "Disabled" : ChatColor.GREEN + "Enabled"), 1, (playerData.isHidingScoreboard() ? (byte)8 : (byte)10), ChatColor.GRAY + "" + (playerData.isHidingScoreboard() ? "Click to allow your scoreboard to show." : "Click to deny your scoreboard from showing.")).getItem());
				player.sendMessage(ChatColor.LIGHT_PURPLE + (playerData.isHidingScoreboard() ? "You are now hiding your scoreboard." : "You are now showing your scoreboard."));
			} else if(event.getPosition() == 12) {
				playerData.setAllowSpectators(!playerData.isAllowSpectators());
				event.getInventory().setItem(12, new ItemBuilder(Material.INK_SACK, (playerData.isAllowSpectators() ? ChatColor.GRAY + "Disabled" : ChatColor.GREEN + "Enabled"), 1, (playerData.isAllowSpectators() ? (byte)8 : (byte)10), ChatColor.GRAY + "" + (playerData.isAllowSpectators() ? "Click to allow other players to spectate your matches." : "Click to deny other players to spectating your matches.")).getItem());
				player.sendMessage(ChatColor.LIGHT_PURPLE + (playerData.isAllowSpectators() ? "You are now denying players to spectate your matches." : "You are now allowing players to spectate your matches."));
			} else if(event.getPosition() == 14) {
				playerData.setHidingSounds(!playerData.isHidingSounds());
				event.getInventory().setItem(14, new ItemBuilder(Material.INK_SACK, (playerData.isHidingSounds() ? ChatColor.GRAY + "Disabled" : ChatColor.GREEN + "Enabled"), 1, (playerData.isHidingSounds() ? (byte)8 : (byte)10), ChatColor.GRAY + "" + (playerData.isHidingSounds() ? "Click to allow indicator sounds." : "Click to deny indicator sounds.")).getItem());
				player.sendMessage(ChatColor.LIGHT_PURPLE + (playerData.isHidingSounds() ? "You are now muting indicator sounds." : "You are now hearing indicator sounds."));
			} else if(event.getPosition() == 16) {
				playerData.setHidingRequests(!playerData.isHidingRequests());
				event.getInventory().setItem(16, new ItemBuilder(Material.INK_SACK, (playerData.isHidingRequests() ? ChatColor.GRAY + "Disabled" : ChatColor.GREEN + "Enabled"), 1, (playerData.isHidingRequests() ? (byte)8 : (byte)10), ChatColor.GRAY + "" + (playerData.isHidingRequests() ? "Click to allow recieving duel/party requests." : "Click to deny recieving duel/party requests.")).getItem());
				player.sendMessage(ChatColor.LIGHT_PURPLE + (playerData.isHidingRequests() ? "You are now hiding requests." : "You are now showing requests."));
			} else {
				player.sendMessage(ChatColor.RED + "Unknown option.");
			}

			event.setWillClose(false);
			event.setWillDestroy(false);

			player.updateInventory();
		}, player);

		menu.setOption(1, new ItemStack(Material.ANVIL), ChatColor.YELLOW + "Scoreboard", ChatColor.GRAY + "Allow or deny your scoreboard", ChatColor.GRAY + "to show.");
		menu.setOption(3, new ItemStack(Material.ANVIL), ChatColor.YELLOW + "Spectators", ChatColor.GRAY + "Allow or deny other players", ChatColor.GRAY + "to spectate your matches.");
		menu.setOption(5, new ItemStack(Material.ANVIL), ChatColor.YELLOW + "Sounds", ChatColor.GRAY + "Allow or deny the ability to", ChatColor.GRAY + "hear indicator sounds.");
		menu.setOption(7, new ItemStack(Material.ANVIL), ChatColor.YELLOW + "Requests", ChatColor.GRAY + "Allow or deny recieving duel", ChatColor.GRAY + "or party requests.");

		menu.setOption(10, new ItemStack(Material.INK_SACK, 1, (playerData.isHidingScoreboard() ? (byte)8 : (byte)10)), (playerData.isHidingScoreboard() ? ChatColor.GRAY + "Disabled" : ChatColor.GREEN + "Enabled"), ChatColor.GRAY + "" + (playerData.isHidingScoreboard() ? "Click to allow your scoreboard to show." : "Click to deny your scoreboard from showing."));
		menu.setOption(12, new ItemStack(Material.INK_SACK, 1, (playerData.isAllowSpectators() ? (byte)8 : (byte)10)), (playerData.isAllowSpectators() ? ChatColor.GRAY + "Disabled" : ChatColor.GREEN + "Enabled"), ChatColor.GRAY + "" + (playerData.isAllowSpectators() ? "Click to allow other players to spectate your matches." : "Click to deny other players to spectating your matches."));
		menu.setOption(14, new ItemStack(Material.INK_SACK, 1, (playerData.isHidingSounds() ? (byte)8 : (byte)10)), (playerData.isHidingSounds() ? ChatColor.GRAY + "Disabled" : ChatColor.GREEN + "Enabled"), ChatColor.GRAY + "" + (playerData.isHidingSounds() ? "Click to allow indicator sounds." : "Click to deny indicator sounds."));
		menu.setOption(16, new ItemStack(Material.INK_SACK, 1, (playerData.isHidingRequests() ? (byte)8 : (byte)10)), (playerData.isHidingRequests() ? ChatColor.GRAY + "Disabled" : ChatColor.GREEN + "Enabled"), ChatColor.GRAY + "" + (playerData.isHidingRequests() ? "Click to allow recieving duel/party requests." : "Click to deny recieving duel/party requests."));

		menu.setDestroyOnClose(true);
		menu.open(player);
	}

}