package net.propvp.practice.game.arena;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CuboidSelection {

	public static Map<UUID, Location> sel1 = new HashMap<UUID, Location>();
	public static Map<UUID, Location> sel2 = new HashMap<UUID, Location>();
	
	public static void setSelectionOne(Player player, Location loc) {
		sel1.put(player.getUniqueId(), loc);
	}
	
	public static void setSelectionTwo(Player player, Location loc) {
		sel2.put(player.getUniqueId(), loc);
	}
	
	public static Location getSelectionOne(Player player) {
		return sel1.get(player.getUniqueId());
	}
	
	public static Location getSelectionTwo(Player player) {
		return sel2.get(player.getUniqueId());
	}
	
	public static boolean bothPointsSet(Player player) {
		return (sel1.containsKey(player.getUniqueId()) && sel2.containsKey(player.getUniqueId()));
	}
	
}