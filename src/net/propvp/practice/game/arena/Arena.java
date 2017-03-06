package net.propvp.practice.game.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import net.propvp.practice.Practice;
import net.propvp.practice.utils.LocationUtil;

public class Arena {

	private String name;
	private Location spawn1;
	private Location spawn2;
	private Cuboid cuboid;
	private List<Location> safeBlocks;
	private boolean active = false;
	private boolean setup = false;
	private boolean isMultiUse = false;

	public Arena(String name) {
		this.name = name;
		this.spawn1 = null;
		this.spawn2 = null;
		this.cuboid = null;
		this.safeBlocks = null;
		this.active = false;
		this.setup = false;
		this.isMultiUse = false;
	}

	public Arena(String name, String spawn1, String spawn2, String cuboid1, String cuboid2, boolean isMultiUse) {
		this.name = name;
		this.setup = true;
		this.isMultiUse = isMultiUse;
		
		try {
			if(spawn1 != null) this.spawn1 = LocationUtil.getLocation(spawn1);
			if(spawn2 != null) this.spawn2 = LocationUtil.getLocation(spawn2);
		} catch (ArrayIndexOutOfBoundsException e) {
			this.spawn1 = null;
			this.spawn2 = null;
			this.setup = false;
			Practice.getInstance().getLogger().severe("Failed loading arena '" + name + "' spawn points.");
		}
		
		try {
			if(cuboid1 == null || cuboid2 == null) throw new ArrayIndexOutOfBoundsException();
			this.cuboid = new Cuboid(LocationUtil.getLocation(cuboid1), LocationUtil.getLocation(cuboid2));
		} catch (ArrayIndexOutOfBoundsException e) {
			this.cuboid = null;
			this.setup = false;
			Practice.getInstance().getLogger().severe("Failed loading arena '" + name + "' cuboid points.");
		}
		
		if(cuboid != null) {
			try {
				this.safeBlocks = new ArrayList<Location>();
				
				for(Block b : this.cuboid.getBlocks()) {
					if(b.getType() != Material.AIR) {
						this.safeBlocks.add(b.getLocation());
					}
				}
			} catch (NullPointerException e) {
				this.safeBlocks = null;
				this.setup = false;
				Practice.getInstance().getLogger().severe("Failed loading arena '" + name + "' safe blocks.");
			}
		}
	}

	public String getName() {
		return name;
	}

	public Location getSpawn1() {
		return spawn1;
	}

	public Location getSpawn2() {
		return spawn2;
	}
	
	public Cuboid getCuboid() {
		return cuboid;
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isMultiUse() {
		return isMultiUse;
	}
	
	public void setMultiUse(boolean bool) {
		this.isMultiUse = bool;
	}

	public void setSpawn1(Location location) {
		this.spawn1 = location;
	}

	public void setSpawn2(Location location) {
		this.spawn2 = location;
	}
	
	public void setCuboid(Cuboid cuboid) {
		this.cuboid = cuboid;
	}
	
	public boolean isSafeBlock(Location loc) {
		return safeBlocks.contains(loc);
	}

	public boolean isSetup() {
		return setup;
	}

	public void save() {
		FileConfiguration config = Practice.getInstance().getArenaManager().getConfig();
		
		if(!config.isConfigurationSection("arenas")) {
			config.createSection("arenas");
		}
		
		if(!config.isConfigurationSection("arenas." + name)) {
			config.createSection("arenas." + name);
		}

		if(spawn1 != null) {
			config.set("arenas." + name + ".spawn1", LocationUtil.getString(spawn1));
		}

		if(spawn2 != null) {
			config.set("arenas." + name + ".spawn2", LocationUtil.getString(spawn2));
		}
		
		if(cuboid != null) {
			config.set("arenas." + name + ".region1", LocationUtil.getString(this.cuboid.getUpperLocation()));
			config.set("arenas." + name + ".region2", LocationUtil.getString(this.cuboid.getLowerLocation()));
		}
		
		config.set("arenas." + name + ".multiuse", this.isMultiUse);
		
		Practice.getInstance().getArenaManager().saveConfig();
	}
	
	public void remove() {
		Practice.getInstance().getArenaManager().removeArena(name);
		Practice.getInstance().getArenaManager().getConfig().set("arenas." + name, null);
		Practice.getInstance().getArenaManager().saveConfig();
	}

}