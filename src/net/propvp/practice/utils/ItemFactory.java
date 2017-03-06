package net.propvp.practice.utils;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.player.PlayerData;

public class ItemFactory {
	
	public static ConfigurationSection configSection = PracticeConfiguration.getRootConfig().getConfig().getConfigurationSection("items");
	
	@SuppressWarnings("unchecked")
	public static ItemStack createItemStackFromSection(String item) throws Exception {
		return new ItemBuilder(Material.valueOf(configSection.getString(item + ".material")), MessageUtils.color(configSection.getString(item + ".name")), 1, (byte) configSection.getInt(item + ".data"), (List<String>) configSection.getList(item + ".lore")).getItem();
	}

	public static ItemStack getRankedMatchFinder() {
		try {
			return createItemStackFromSection("ranked-menu");
		} catch (Exception e) {
			Practice.getInstance().getLogger().severe("Failed to load itemstack from configuration (ranked-menu)");
			return null;
		}
	}

	public static ItemStack getNormalMatchFinder() {
		try {
			return createItemStackFromSection("normal-menu");
		} catch (Exception e) {
			Practice.getInstance().getLogger().severe("Failed to load itemstack from configuration (normal-menu)");
			return null;
		}
	}
	
	public static ItemStack getPartyMatchFinder() {
		try {
			return createItemStackFromSection("party-menu");
		} catch (Exception e) {
			Practice.getInstance().getLogger().severe("Failed to load itemstack from configuration (party-menu)");
			return null;
		}
	}

	public static ItemStack getKitEditor() {
		try {
			return createItemStackFromSection("kit-editor");
		} catch (Exception e) {
			Practice.getInstance().getLogger().severe("Failed to load itemstack from configuration (normal-menu)");
			return null;
		}
	}

	public static ItemStack getLeaveQueue() {
		try {
			return createItemStackFromSection("leave-queue");
		} catch (Exception e) {
			e.printStackTrace();
			Practice.getInstance().getLogger().severe("Failed to load itemstack from configuration (leave-queue)");
			return null;
		}
	}
	
	public static ItemStack getSpectatorOptions() {
		try {
			return createItemStackFromSection("player-options");
		} catch (Exception e) {
			e.printStackTrace();
			Practice.getInstance().getLogger().severe("Failed to load itemstack from configuration (player-options)");
			return null;
		}
	}

	public static ItemStack getRegionSelector() {
		return new ItemBuilder(Material.RECORD_7, ChatColor.AQUA + "Region Selector", ChatColor.GRAY + "Left click to select pos1.", ChatColor.GRAY + "Right click to select pos2.").getItem();
	}
	
	public static ItemStack getPlayerHider(Player player) {
		PlayerData data = Practice.getInstance().getDataManager().getData(player);
		return new ItemBuilder(Material.INK_SACK, (data.isHidingPlayers() ? ChatColor.RED + "Hiding Players" : ChatColor.GREEN + "Showing Players") + ChatColor.GRAY + " (Right-click)", 1, (data.isHidingPlayers() ? (byte)8 : (byte)10), ChatColor.GRAY + (data.isHidingPlayers() ? "Right click to show players." : "Right click to hide players.")).getItem();
	}

}