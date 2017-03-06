package net.propvp.practice.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.propvp.practice.Practice;

public class InventoryFactory {
	
	public static Map<String, Inventory> invs = new HashMap<String, Inventory>();
	public static List<UUID> opened = new ArrayList<UUID>();
	
	public static ItemStack[] getDefaultInventory(Player player) {
		return new ItemStack[] { ItemFactory.getRankedMatchFinder(), ItemFactory.getNormalMatchFinder(), null, null, null, null, ItemFactory.getSpectatorOptions(), ItemFactory.getKitEditor(), ItemFactory.getPlayerHider(player) };
	}
	
	public static ItemStack[] getLeaderInventory() {
		return new ItemStack[] { ItemFactory.getPartyMatchFinder(), null, null, null, null, null, null, null, null };
	}
	
	public static ItemStack[] getMemberInventory() {
		return new ItemStack[] {};
	}
	
	public static ItemStack[] getQueuedInventory() {
		return new ItemStack[] { null, null, null, null, ItemFactory.getLeaveQueue(), null, null, null, null };
	}
	
	public static ItemStack[] getEmptyArmor() {
		return new ItemStack[] {null, null, null, null};
	}

	public static void storeInv(Player player, boolean dead) {
		Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, player.getName());
		PlayerInventory pinv = player.getInventory();

		for (int i = 9; i <= 35; ++i) {
			inv.setItem(i - 9, pinv.getContents()[i]);
		}

		for (int i = 0; i <= 8; ++i) {
			inv.setItem(i + 27, pinv.getContents()[i]);
		}

		inv.setItem(36, pinv.getHelmet());
		inv.setItem(37, pinv.getChestplate());
		inv.setItem(38, pinv.getLeggings());
		inv.setItem(39, pinv.getBoots());

		if (dead) {
			inv.setItem(48, new ItemBuilder(Material.SKULL_ITEM, ChatColor.RED + "Player Died", "").getItem());
		} else {
			inv.setItem(48, new ItemBuilder(Material.SPECKLED_MELON, ChatColor.GREEN + "Player Health", (int)player.getHealthScale()).getItem());
		}

		inv.setItem(49, new ItemBuilder(Material.COOKED_BEEF, ChatColor.GREEN + "Player Food", player.getFoodLevel()).getItem());

		ItemStack potions = new ItemBuilder(Material.POTION, ChatColor.BLUE + "Potion Effects", player.getActivePotionEffects().size()).getItem();
		ItemMeta imm = potions.getItemMeta();
		List<String> lore = (List<String>)imm.getLore();
		lore.addAll(player.getActivePotionEffects().stream().map(effect -> effect.getType().getName() + " " + (effect.getAmplifier() + 1) + " for " + InventoryUtil.formatSeconds(effect.getDuration() / 20) + "!").collect(Collectors.toList()));
		imm.setLore(lore);
		potions.setItemMeta(imm);
		inv.setItem(50, potions);

		invs.put(player.getName(), inv);

		new BukkitRunnable() {
			public void run() {
				invs.remove(player.getName());
			}
		}.runTaskLater(Practice.getInstance(), 2400L);
	}
	
}