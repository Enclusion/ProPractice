package net.propvp.practice.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.propvp.practice.Practice;
import net.propvp.practice.game.Game;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.EntityHider;

public class EntityListener implements Listener {

	private Practice plugin;

	public EntityListener(Practice plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason() == SpawnReason.NATURAL || event.getSpawnReason() == SpawnReason.DEFAULT) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLaunch(ProjectileLaunchEvent event) {
		if(!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity().getShooter();
		PlayerData data = plugin.getDataManager().getData(player);
		
        if(event.getEntityType() == EntityType.SPLASH_POTION || event.getEntityType() == EntityType.ENDER_PEARL || event.getEntityType() == EntityType.ARROW) {
            EntityHider hider = plugin.getEntityHider();
            
            if(data.getMatch() != null) {
            	Game match = data.getMatch();
            	
            	for(Player p : Bukkit.getOnlinePlayers()) {
            		if(!match.getPlayers().contains(p)) {
            			hider.hideEntity(p, event.getEntity());
            		}
            	}
            }
        }
	}
	
}