package net.propvp.practice.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.propvp.practice.Practice;

public class DataManager implements Listener {

	private Map<Player, PlayerData> data;

	public DataManager() {
		data = new HashMap<Player, PlayerData>();
		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			PlayerData data = new PlayerData(player);
			this.data.put(player, data);
		}
	}
	
	public boolean hasData(Player key) {
		return data.containsKey(key);
	}
	
	public PlayerData getData(Player key) {
		return data.get(key);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(data.containsKey(player)) {
			data.remove(player);
		}
		
		PlayerData pdata = new PlayerData(player);
		data.put(player, pdata);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		data.get(player).saveAccount();
		
		new BukkitRunnable() {
			public void run() {
				if(data.containsKey(player)) {
					data.remove(player);
				}
			}
		}.runTaskLater(Practice.getInstance(), 60L);
	}
	
}