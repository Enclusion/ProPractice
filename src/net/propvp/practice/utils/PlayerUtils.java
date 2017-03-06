package net.propvp.practice.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.player.PlayerData;

public class PlayerUtils {

	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(String playerNameOrUUID) {
		OfflinePlayer player = tryGetFromUUID(playerNameOrUUID);

		if (player != null && (player.hasPlayedBefore() || player.isOnline())) {
			return player;
		}

		player = (OfflinePlayer) Bukkit.getServer().getPlayer(playerNameOrUUID);

		if (player == null) {
			player = Bukkit.getServer().getOfflinePlayer(playerNameOrUUID);
		}

		if (player != null && !player.hasPlayedBefore() && !player.isOnline()) {
			return null;
		}

		return player;
	}

	private static OfflinePlayer tryGetFromUUID(String possibleUUID) {
		UUID uuid;

		try {
			uuid = UUID.fromString(possibleUUID);
		} catch (IllegalArgumentException ignored) {
			return null;
		}

		OfflinePlayer player = (OfflinePlayer) Bukkit.getServer().getPlayer(uuid);

		if (player != null) {
			return player;
		}

		return Bukkit.getServer().getOfflinePlayer(uuid);
	}

	public static void prepareForSpectator(Player player) {
		Practice.getInstance().getEntityHider().hideAllPlayers(player);
		PlayerData data = Practice.getInstance().getDataManager().getData(player);
		data.setSpectating(false);
		data.setMatch(null);

		setDefaults(player);

		player.setAllowFlight(true);
		player.setCanPickupItems(false);
	}

	public static void prepareForSpawn(Player player) {
		PracticeConfiguration.teleportToSpawn(player);
		Practice.getInstance().getEntityHider().showAllPlayers(player);
		PlayerData data = Practice.getInstance().getDataManager().getData(player);
		data.setSpectating(false);
		data.setMatch(null);

		setDefaults(player);
	}

	public static void setDefaults(Player player) {
		for(PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		
		player.getActivePotionEffects().clear();

		player.setGameMode(org.bukkit.GameMode.SURVIVAL);
		player.setAllowFlight(false);
		player.setCanPickupItems(true);
		player.setFireTicks(0);
		player.setMaximumNoDamageTicks(20);
		player.setHealth(20);
		player.getInventory().clear();
		player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
		player.updateInventory();
	}

}