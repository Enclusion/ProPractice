package net.propvp.practice.listener;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.InventoryFactory;
import net.propvp.practice.utils.MessageUtils;

public class PlayerListener implements Listener {

	private Practice plugin;

	public PlayerListener(Practice plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
		if(!(event.getEntity() instanceof Player)) { return; }

		Player player = (Player) event.getEntity();
		PlayerData data = plugin.getDataManager().getData(player);

		if(!data.inMatch()) { event.setFoodLevel(20); }
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();
		PlayerData data = plugin.getDataManager().getData(player);

		if(!data.inMatch()) {
			event.getDrops().clear();
			event.setKeepInventory(true);
		}
		
		event.setDeathMessage(null);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.getDataManager().getData(player);

		if(!data.inMatch()) {
			Practice.getInstance().getConfiguration();
			if(PracticeConfiguration.isSpawnSet()) {
				event.setRespawnLocation(PracticeConfiguration.getSpawnLocation());
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		new BukkitRunnable() {
			public void run() {
				PracticeConfiguration.teleportToSpawn(player);
				player.getInventory().clear();
				player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
				player.getInventory().setContents(InventoryFactory.getDefaultInventory(player));
				player.updateInventory();
			}
		}.runTaskLater(Practice.getInstance(), 1L);

		event.setJoinMessage(null);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		event.setLeaveMessage(null);
	}

	@EventHandler
	public void onDurability(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.getDataManager().getData(player);

		if(!data.inMatch()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.getDataManager().getData(player);

		if(!data.inMatch()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPearl(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerData data = Practice.getInstance().getDataManager().getData(player);
		
		if (((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) && (event.getItem() != null) && (event.getItem().getType() == Material.ENDER_PEARL)) {
			if(!data.inMatch()) { event.setCancelled(true); return; }
			
			if(!data.getMatch().hasStarted()) {
				player.sendMessage(ChatColor.RED + "You cannot use an enderpearl until the match has started.");
				event.setCancelled(true);
				return;
			}
			
			if(data.getScoreboardUser().getEnderpearlTimer().isActive()) {
				DecimalFormat format = new DecimalFormat("##.#");
				String timeLeft = format.format(((double)data.getScoreboardUser().getEnderpearlTimer().getTimeLeft() / 1000));
				
				player.sendMessage(MessageUtils.color(ChatColor.RED + "You cannot use an enderpearl for another " + ChatColor.YELLOW + "%time%" + ChatColor.RED + " seconds.").replace("%time%", timeLeft));
				event.setCancelled(true);
				return;
			}

			data.getScoreboardUser().getEnderpearlTimer().setTimerEnd(System.currentTimeMillis() + 1000 * 15);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			PlayerData data = plugin.getDataManager().getData((Player)event.getEntity());

			if(data.isSpectating()) {
				event.setCancelled(true);
				return;
			}

			if(!data.inMatch()) {
				event.setCancelled(true);
				return;
			}

			if(event.getDamager() instanceof Player) {
				Player damager = (Player) event.getDamager();
				PlayerData ddata = plugin.getDataManager().getData(damager);

				if(ddata.isSpectating()) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

}