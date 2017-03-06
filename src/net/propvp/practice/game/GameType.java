package net.propvp.practice.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.propvp.practice.Practice;
import net.propvp.practice.player.PlayerInv;
import net.propvp.practice.utils.IconMenu;
import net.propvp.practice.utils.InventoryUtil;
import net.propvp.practice.utils.ItemBuilder;

public class GameType {

	private String name;
	private String displayName;
	private ItemStack displayIcon;
	private Integer displayOrder;
	private Integer hitDelay;
	private boolean isEditable;
	private boolean isRegeneration;
	private boolean isHunger;
	private boolean isBuilding;
	private boolean isBreaking;
	private PlayerInv inventory;
	
	public GameType(String name) {
		this.name = name;
		this.inventory = null;
		this.displayIcon = new ItemStack(Material.ANVIL);
		this.displayName = ChatColor.AQUA + name;
		this.displayOrder = -1;
		this.isEditable = false;
		this.isRegeneration = true;
		this.isHunger = true;
		this.isBuilding = false;
		this.isBreaking = false;
		this.hitDelay = 20;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public ItemStack getIcon() {
		return new ItemBuilder(displayIcon, displayName).getItem();
	}
	
	public void setIcon(ItemStack is) {
		this.displayIcon = is;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(int order) {
		this.displayOrder = order;
	}
	
	public PlayerInv getInventory() {
		return inventory;
	}
	
	public boolean isEditable() {
		return isEditable;
	}
	
	public boolean isRegeneration() {
		return isRegeneration;
	}
	
	public boolean isHunger() {
		return isHunger;
	}
	
	public boolean isBuilding() {
		return isBuilding;
	}
	
	public boolean isBreaking() {
		return isBreaking;
	}
	
	public int getHitDelay() {
		return hitDelay;
	}
	
	public void setStartingInventory(PlayerInv inventory) {
		this.inventory = inventory;
	}
	
	public void setEditable(boolean bool) {
		this.isEditable = bool;
	}
	
	public void setRegeneration(boolean bool) {
		this.isRegeneration = bool;
	}
	
	public void setHunger(boolean bool) {
		this.isHunger = bool;
	}
	
	public void setBuilding(boolean bool) {
		this.isBuilding = bool;
	}
	
	public void setBreaking(boolean bool) {
		this.isBreaking = bool;
	}
	
	public void setHitDelay(Integer delay) {
		this.hitDelay = delay;
	}

	public void openSettings(Player player) {
		IconMenu menu = new IconMenu(ChatColor.GRAY + "Settings of " + ChatColor.WHITE + this.name, 18, event -> {
			if(!event.getItemStack().getType().equals(Material.INK_SACK)) return;
			
			if(event.getPosition() == 9) {
				isBuilding = !isBuilding;
				event.getMenu().setOption(9, new ItemStack(Material.INK_SACK, 1, (isBuilding ? (byte)10 : (byte)8)), ChatColor.GRAY + "Building " + (isBuilding ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), ChatColor.GRAY + "" + (isBuilding ? "Click to deny the construction of blocks." : "Click to allow the construction of blocks."));
				event.getInventory().setItem(9, new ItemBuilder(Material.INK_SACK, (isBuilding ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), 1, (isBuilding ? (byte)10 : (byte)8), ChatColor.GRAY + "" + (isBuilding ? "Click to deny the construction of blocks." : "Click to allow the construction of blocks.")).getItem());
				player.sendMessage(ChatColor.GOLD + "You have set " + ChatColor.YELLOW + name + ChatColor.GOLD + "'s building setting to " + isBuilding + ".");
			} else if(event.getPosition() == 11) {
				isBreaking = !isBreaking;
				event.getMenu().setOption(11, new ItemStack(Material.INK_SACK, 1, (isBreaking ? (byte)10 : (byte)8)), ChatColor.GRAY + "Breaking " + (isBreaking ? ChatColor.GREEN + "" : ChatColor.GRAY + ""), ChatColor.GRAY + "" + (isBreaking ? "Click to deny the destruction of blocks." : "Click to allow the destruction of blocks."));
				event.getInventory().setItem(11, new ItemBuilder(Material.INK_SACK, (isBreaking ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), 1, (isBreaking ? (byte)10 : (byte)8), ChatColor.GRAY + "" + (isBreaking ? "Click to deny the destruction of blocks." : "Click to allow the destruction of blocks.")).getItem());
				player.sendMessage(ChatColor.GOLD + "You have set " + ChatColor.YELLOW + name + ChatColor.GOLD + "'s breaking setting to " + isBreaking + ".");
			} else if(event.getPosition() == 13) {
				isRegeneration = !isRegeneration;
				event.getMenu().setOption(13, new ItemStack(Material.INK_SACK, 1, (isRegeneration ? (byte)10 : (byte)8)), ChatColor.GRAY + "Regeneration " + (isRegeneration ? ChatColor.GREEN + "" : ChatColor.GRAY + ""), ChatColor.GRAY + "" + (isRegeneration ? "Click to deny the regeneration of health." : "Click to allow the regeneration of health."));
				event.getInventory().setItem(13, new ItemBuilder(Material.INK_SACK, (isRegeneration ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), 1, (isRegeneration ? (byte)10 : (byte)8), ChatColor.GRAY + "" + (isRegeneration ? "Click to deny the regeneration of health." : "Click to allow the regeneration of health.")).getItem());
				player.sendMessage(ChatColor.GOLD + "You have set " + ChatColor.YELLOW + name + ChatColor.GOLD + "'s regeneration setting to " + isRegeneration + ".");
			} else if(event.getPosition() == 15) {
				isHunger = !isHunger;
				event.getMenu().setOption(15, new ItemStack(Material.INK_SACK, 1, (isHunger ? (byte)10 : (byte)8)), ChatColor.GRAY + "Hunger " + (isHunger ? ChatColor.GREEN + "" : ChatColor.GRAY + ""), ChatColor.GRAY + "" + (isHunger ? "Click to deny the change of hunger." : "Click to allow the change of hunger."));
				event.getInventory().setItem(15, new ItemBuilder(Material.INK_SACK, (isHunger ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), 1, (isHunger ? (byte)10 : (byte)8), ChatColor.GRAY + "" + (isHunger ? "Click to deny the change of hunger." : "Click to allow the change of hunger.")).getItem());
				player.sendMessage(ChatColor.GOLD + "You have set " + ChatColor.YELLOW + name + ChatColor.GOLD + "'s hunger setting to " + isHunger + ".");
			} else if(event.getPosition() == 17) {
				isEditable = !isEditable;
				event.getMenu().setOption(17, new ItemStack(Material.INK_SACK, 1, (isEditable ? (byte)10 : (byte)8)), ChatColor.GRAY + "Editable " + (isEditable ? ChatColor.GREEN + "" : ChatColor.GRAY + ""), ChatColor.GRAY + "" + (isEditable ? "Click to deny the editing of the default kit." : "Click to allow the editing of the default kit."));
				event.getInventory().setItem(17, new ItemBuilder(Material.INK_SACK, (isEditable ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), 1, (isEditable ? (byte)10 : (byte)8), ChatColor.GRAY + "" + (isEditable ? "Click to deny the editing of the default kit." : "Click to allow the editing of the default kit.")).getItem());
				player.sendMessage(ChatColor.GOLD + "You have made " + ChatColor.YELLOW + name + ChatColor.GOLD + "'s default kit " + (isEditable ? "editable" : "non-editable") + ".");
			} else {
				player.sendMessage(ChatColor.RED + "Unknown option.");
			}
			
			event.setWillClose(false);
			event.setWillDestroy(false);
			
			player.updateInventory();
			this.save();
		}, player);

		menu.setOption(0, new ItemStack(Material.WOOD), ChatColor.YELLOW + "Building", ChatColor.GRAY + "Allow or deny the construction", ChatColor.GRAY + "of blocks.");
		menu.setOption(2, new ItemStack(Material.DIAMOND_PICKAXE), ChatColor.YELLOW + "Breaking", ChatColor.GRAY + "Allow or deny the destruction", ChatColor.GRAY + "of blocks.");
		menu.setOption(4, new ItemStack(Material.GOLDEN_APPLE), ChatColor.YELLOW + "Regeneration", ChatColor.GRAY + "Allow or deny the regeneration", ChatColor.GRAY + "of a player's health level.");
		menu.setOption(6, new ItemStack(Material.COOKED_BEEF), ChatColor.YELLOW + "Hunger", ChatColor.GRAY + "Allow or deny the change of a", ChatColor.GRAY + "player's hunger level.");
		menu.setOption(8, new ItemStack(Material.BOOK), ChatColor.YELLOW + "Editable", ChatColor.GRAY + "Allow or deny a player to edit", ChatColor.GRAY + "the default kit.");
		
		menu.setOption(9, new ItemStack(Material.INK_SACK, 1, (isBuilding ? (byte)10 : (byte)8)), (isBuilding ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), ChatColor.GRAY + "" + (isBuilding ? "Click to deny the construction of blocks." : "Click to allow the construction of blocks."));
		menu.setOption(11, new ItemStack(Material.INK_SACK, 1, (isBreaking ? (byte)10 : (byte)8)), (isBreaking ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), ChatColor.GRAY + "" + (isBreaking ? "Click to deny the destruction of blocks." : "Click to allow the destruction of blocks."));
		menu.setOption(13, new ItemStack(Material.INK_SACK, 1, (isRegeneration ? (byte)10 : (byte)8)), (isRegeneration ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), ChatColor.GRAY + "" + (isRegeneration ? "Click to deny the regeneration of health." : "Click to allow the regeneration of health."));
		menu.setOption(15, new ItemStack(Material.INK_SACK, 1, (isHunger ? (byte)10 : (byte)8)), (isHunger ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), ChatColor.GRAY + "" + (isHunger ? "Click to deny the change of hunger." : "Click to allow the change of hunger."));
		menu.setOption(17, new ItemStack(Material.INK_SACK, 1, (isEditable ? (byte)10 : (byte)8)), (isEditable ? ChatColor.GREEN + "Enabled" : ChatColor.GRAY + "Disabled"), ChatColor.GRAY + "" + (isEditable ? "Click to deny the editing of the default kit." : "Click to allow the editing of the default kit."));

		menu.setDestroyOnClose(true);
		menu.open(player);
	}

	public void save() {
		FileConfiguration config = Practice.getInstance().getGameManager().getConfig();
		
		if(!config.isConfigurationSection("gametypes." + name)) {
			config.createSection("gametypes." + name);
		}
		
		config.set("gametypes." + name + ".display-name", getDisplayName());
		config.set("gametypes." + name + ".display-icon.material", getIcon().getType().toString());
		config.set("gametypes." + name + ".display-icon.data", getIcon().getDurability());
		config.set("gametypes." + name + ".display-order", getDisplayOrder());
		
		if(inventory == null) {
			config.set("gametypes." + name + ".items", "null");
		} else {
			config.set("gametypes." + name + ".items", InventoryUtil.playerInventoryToString(getInventory()));
		}
			
		config.set("gametypes." + name + ".editable", isEditable());
		config.set("gametypes." + name + ".regeneration", isRegeneration());
		config.set("gametypes." + name + ".hunger", isHunger());
		config.set("gametypes." + name + ".building", isBuilding());
		config.set("gametypes." + name + ".breaking", isBreaking());
		config.set("gametypes." + name + ".hit-delay", getHitDelay());
		Practice.getInstance().getGameManager().saveConfig();
	}
	
	public void remove() {
		Practice.getInstance().getGameManager().getConfig().set("gametypes." + name, null);
		Practice.getInstance().getGameManager().saveConfig();
	}
	
}