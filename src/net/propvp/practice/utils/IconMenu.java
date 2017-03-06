package net.propvp.practice.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.propvp.practice.Practice;

public class IconMenu implements Listener {

	private String name;
	private int size;
	private OptionClickEventHandler handler;
	private String[] optionNames;
	private ItemStack[] optionIcons;
	private UUID[] optionUUIDs;
	private Player owner;
	private boolean destroyOnClose;
	List<Player> open;

	public IconMenu(String name, int size, OptionClickEventHandler handler, Player owner) {
		this.name = name;
		this.size = size;
		this.handler = handler;
		this.optionNames = new String[size];
		this.optionIcons = new ItemStack[size];
		this.optionUUIDs = new UUID[size];
		this.open = new ArrayList<Player>();
		this.owner = owner;
		this.destroyOnClose = false;
		Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
	}

	public IconMenu(String name, int size, OptionClickEventHandler handler, Player owner, boolean destroyOnClose) {
		this.name = name;
		this.size = size;
		this.handler = handler;
		this.optionNames = new String[size];
		this.optionIcons = new ItemStack[size];
		this.optionUUIDs = new UUID[size];
		this.open = new ArrayList<Player>();
		this.owner = owner;
		this.destroyOnClose = destroyOnClose;
		Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
	}

	public IconMenu setOption(int position, Material icon, String name, String... info) {
		final ItemStack item = this.setItemNameAndLore(icon, name, info);

		if (this.optionIcons[position] != null && this.optionIcons[position].getType() == icon) {
			this.updateItem(position, item.getItemMeta());
		}

		this.optionIcons[position] = item;
		this.optionNames[position] = name;

		if(info.length >= 3) {
			if(info[2] != null) {
				if(info[2] == "") {
					this.optionUUIDs[position] = null;
				} else {
					if(isUUID(HiddenStringUtil.extractHiddenString(info[2]))) {
						this.optionUUIDs[position] = UUID.fromString(HiddenStringUtil.extractHiddenString(info[2]));
					} else {
						this.optionUUIDs[position] = null;
					}
				}
			}
		}

		return this;
	}

	public IconMenu setOption(int position, ItemStack icon, String name, String... info) {
		final ItemStack item = this.setItemNameAndLore(icon, name, info);

		if(this.optionIcons[position] != null && this.optionIcons[position] == icon) {
			this.updateItem(position, item.getItemMeta());
		}

		this.optionIcons[position] = item;
		this.optionNames[position] = name;

		if(info.length >= 3) {
			if(info[2] != null) {
				if(info[2] == "") {
					this.optionUUIDs[position] = null;
				} else {
					if(isUUID(HiddenStringUtil.extractHiddenString(info[2]))) {
						this.optionUUIDs[position] = UUID.fromString(HiddenStringUtil.extractHiddenString(info[2]));
					} else {
						this.optionUUIDs[position] = null;
					}
				}
			}
		}

		return this;
	}

	public IconMenu setOption(int position, ItemBuilder icon) {
		final ItemStack item = icon.getItem();

		if(this.optionIcons[position] != null && this.optionIcons[position] == icon.getItem()) {
			this.updateItem(position, item.getItemMeta());
		}
		
		if(item.getItemMeta().getDisplayName() == null) {
			Bukkit.getLogger().severe("Item position " + position + " has no display name.");
		}

		this.optionIcons[position] = item;
		this.optionNames[position] = item.getItemMeta().getDisplayName();

		if(icon.getItem().getItemMeta().getLore().isEmpty()) { return this; }

		String[] info = new String[icon.getItem().getItemMeta().getLore().size()];

		int i = 0;

		for(String s : icon.getItem().getItemMeta().getLore()) {
			info[i] = s;
			i++;
		}

		if(info.length >= 3) {
			if(info[2] != null) {
				if(info[2] == "") {
					this.optionUUIDs[position] = null;
				} else {
					if(isUUID(HiddenStringUtil.extractHiddenString(info[2]))) {
						this.optionUUIDs[position] = UUID.fromString(HiddenStringUtil.extractHiddenString(info[2]));
					} else {
						this.optionUUIDs[position] = null;
					}
				}
			}
		}

		return this;
	}

	public void open(Player player) {
		Inventory inventory = Bukkit.createInventory((InventoryHolder)player, this.size, this.name);

		for(int i = 0; i < this.optionIcons.length; ++i) {
			if(this.optionIcons[i] != null) {
				inventory.setItem(i, this.optionIcons[i]);
			}
		}

		this.open.add(player);
		player.openInventory(inventory);
	}

	public void destroy() {
		HandlerList.unregisterAll((Listener)this);
		this.handler = null;
		this.optionNames = null;
		this.optionIcons = null;
	}

	public void setDestroyOnClose(boolean bool) {
		this.destroyOnClose = bool;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equals(this.name)) {
			event.setCancelled(true);
			int slot = event.getRawSlot();
			
			if (slot >= 0 && slot < this.size && this.optionIcons[slot] != null) {
				OptionClickEvent e = new OptionClickEvent((ItemStack)event.getCurrentItem(), (Player)event.getWhoClicked(), slot, this.optionNames[slot], this.optionUUIDs[slot], this.owner, this, event.getInventory());
				this.handler.onOptionClick(e);

				if (e.willClose()) {
					Player p = (Player) event.getWhoClicked();
					Bukkit.getScheduler().scheduleSyncDelayedTask(Practice.getInstance(), (Runnable)new Runnable() {
						@Override
						public void run() {
							p.closeInventory();
						}
					}, 1L);
				}

				if (e.willDestroy()) {
					this.destroy();
				}
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if(this.open.contains(event.getPlayer())) {
			this.open.remove(event.getPlayer());
		}

		if(this.destroyOnClose) {
			destroy();
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if(this.open.contains(event.getPlayer())) {
			this.open.remove(event.getPlayer());
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		if(this.open.contains(event.getPlayer())) {
			this.open.remove(event.getPlayer());
		}
	}

	private ItemStack setItemNameAndLore(Material material, String name, String[] lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		im.setLore((List<String>)Arrays.asList(lore));
		item.setItemMeta(im);
		return item;
	}

	private ItemStack setItemNameAndLore(ItemStack itemstack, String name, String[] lore) {
		ItemStack item = itemstack;
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore((List<String>)Arrays.asList(lore));
		item.setItemMeta(im);
		return item;
	}

	public void updateItem(final int slot, final ItemMeta imm) {
		for (final Player p : this.open) {
			p.getOpenInventory().getItem(slot).setItemMeta(imm);
			p.updateInventory();
		}
	}

	public ItemStack getItem(final int slot) {
		return this.optionIcons[slot];
	}

	public String getInventoryName() {
		return this.name;
	}

	public void resetOptions() {
		this.optionNames = new String[this.size];
		this.optionIcons = new ItemStack[this.size];
	}

	public int numberOfOptions() {
		return this.optionIcons.length;
	}

	public void updateName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}

	public void updateSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}

	public boolean isUUID(String string) {
		try {
			UUID.fromString(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public Player getOwner() {
		return owner;
	}

	public class OptionClickEvent
	{
		private Player player;
		private int position;
		private String name;
		private UUID uuid;
		private Player owner;
		private boolean close;
		private boolean destroy;
		private ItemStack icon;
		private IconMenu menu;
		private Inventory inventory;

		public OptionClickEvent(final ItemStack icon, final Player player, final int position, final String name, final UUID uuid, final Player owner, final IconMenu menu, final Inventory inventory) {
			this.icon = icon;
			this.player = player;
			this.position = position;
			this.name = name;
			this.uuid = uuid;
			this.owner = owner;
			this.close = true;
			this.destroy = false;
			this.menu = menu;
			this.inventory = inventory;
		}

		public ItemStack getItemStack() {
			return this.icon;
		}

		public Player getPlayer() {
			return this.player;
		}

		public int getPosition() {
			return this.position;
		}

		public String getName() {
			return this.name;
		}

		public UUID getUUID() {
			return this.uuid;
		}

		public Player getOwner() {
			return this.owner;
		}

		public boolean willClose() {
			return this.close;
		}

		public boolean willDestroy() {
			return this.destroy;
		}

		public void setWillClose(final boolean close) {
			this.close = close;
		}

		public void setWillDestroy(final boolean destroy) {
			this.destroy = destroy;
		}

		public IconMenu getMenu() {
			return this.menu;
		}

		public Inventory getInventory() {
			return inventory;
		}
	}

	public interface OptionClickEventHandler
	{
		void onOptionClick(final OptionClickEvent p0);
	}

}