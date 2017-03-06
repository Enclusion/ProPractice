package net.propvp.practice.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.game.GameType;
import net.propvp.practice.utils.EntityHider;
import net.propvp.practice.utils.IconMenu;
import net.propvp.practice.utils.InventoryFactory;
import net.propvp.practice.utils.MessageUtils;
import net.propvp.practice.utils.PlayerUtils;

public class EditorManager implements Listener {
	private HashMap<Player, GameType> editingList;
	private HashMap<Player, PlayerKit> renaming;

	public EditorManager() {
		editingList = new HashMap<Player, GameType>();
		renaming = new HashMap<Player, PlayerKit>();

		Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
	}

	public void beginEditing(Player player, GameType gameType) {
		if(gameType == null) return;

		if(!PracticeConfiguration.isEditorSet()) {
			player.sendMessage(ChatColor.RED + "The server has not been setup properly. Please contact an administrator.");
			return;
		}

		PracticeConfiguration.teleportToEditor(player);
		Practice.getInstance().getEntityHider().hideAllPlayers(player);

		editingList.put(player, gameType);

		PlayerInv startinv = gameType.getInventory();

		if(startinv != null) {
			if(startinv.getArmorContents() != null) {
				player.getInventory().setArmorContents(startinv.getArmorContents());
			}

			if(startinv.getContents() != null) {
				player.getInventory().setContents(startinv.getContents());
			}
		}

		player.updateInventory();

		player.sendMessage("");
		player.sendMessage(MessageUtils.getTranslation("editor.started-editing").replace("$gametype", gameType.getName()));
		player.sendMessage("");
	}

	private IconMenu getOptionMenu(Player player, GameType gameType) {
		PlayerData pdata = Practice.getInstance().getDataManager().getData(player);

		IconMenu menu = new IconMenu(MessageUtils.getTranslation("editor.kit-options"), 27, event -> {
			if(event.getPlayer() != event.getOwner()) {
				return;
			}

			String itemName = ChatColor.stripColor(event.getName());
			String[] itemNameA = itemName.split(" ");
			String option = itemNameA[0] + " " + itemNameA[1] + " ";
			String kitNumber = itemNameA[2];

			if(itemName.equals("")) {
				return;
			} else {
				switch(option) {
				case "Save kit: ": {
					PlayerKit kit = new PlayerKit(kitNumber, PlayerInv.fromPlayerInventory(player.getInventory()));
					pdata.saveKit(gameType, kit);
					player.sendMessage(MessageUtils.getTranslation("editor.saved-kit").replace("$kit", kitNumber));
					break;
				}
				case "Load kit: ": {
					PlayerKit kit = pdata.getKit(gameType, kitNumber);

					if(kit.getInv().getArmorContents() != null) {
						player.getInventory().setArmorContents(kit.getInv().getArmorContents());
					}

					if(kit.getInv().getContents() != null) {
						player.getInventory().setContents(kit.getInv().getContents());
					}

					player.updateInventory();
					player.sendMessage(MessageUtils.getTranslation("editor.loaded-kit").replace("$kit", kitNumber));
					break;
				}
				case "Delete kit: ": {
					PlayerKit kit = pdata.getKit(gameType, kitNumber);
					pdata.removeKit(gameType, kit);
					player.sendMessage(MessageUtils.getTranslation("editor.deleted-kit").replace("$kit", kitNumber));
					break;
				}
				}

				player.closeInventory();
				return;
			}
		}, player, true);

		menu.setOption(0, new ItemStack(Material.CHEST, 1, (short)8), MessageUtils.getTranslation("editor.menu-save-kit").replace("$kit", "1"), "");
		menu.setOption(2, new ItemStack(Material.CHEST, 1, (short)8), MessageUtils.getTranslation("editor.menu-save-kit").replace("$kit", "2"), "");
		menu.setOption(4, new ItemStack(Material.CHEST, 1, (short)8), MessageUtils.getTranslation("editor.menu-save-kit").replace("$kit", "3"), "");
		menu.setOption(6, new ItemStack(Material.CHEST, 1, (short)8), MessageUtils.getTranslation("editor.menu-save-kit").replace("$kit", "4"), "");
		menu.setOption(8, new ItemStack(Material.CHEST, 1, (short)8), MessageUtils.getTranslation("editor.menu-save-kit").replace("$kit", "5"), "");

		if(pdata.hasKit(gameType, "1")) {
			menu.setOption(9, new ItemStack(Material.ENCHANTED_BOOK), MessageUtils.getTranslation("editor.menu-load-kit").replace("$kit", "1"), "");
			menu.setOption(18, new ItemStack(Material.FIRE), MessageUtils.getTranslation("editor.menu-delete-kit").replace("$kit", "1"), "");
		} else {
			menu.setOption(9, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
			menu.setOption(18, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
		}

		if(pdata.hasKit(gameType, "2")) {
			menu.setOption(11, new ItemStack(Material.ENCHANTED_BOOK), MessageUtils.getTranslation("editor.menu-load-kit").replace("$kit", "2"), "");
			menu.setOption(20, new ItemStack(Material.FIRE), MessageUtils.getTranslation("editor.menu-delete-kit").replace("$kit", "2"), "");
		} else {
			menu.setOption(11, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
			menu.setOption(20, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
		}

		if(pdata.hasKit(gameType, "3")) {
			menu.setOption(13, new ItemStack(Material.ENCHANTED_BOOK), MessageUtils.getTranslation("editor.menu-load-kit").replace("$kit", "3"), "");
			menu.setOption(22, new ItemStack(Material.FIRE), MessageUtils.getTranslation("editor.menu-delete-kit").replace("$kit", "3"), "");
		} else {
			menu.setOption(13, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
			menu.setOption(22, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
		}

		if(pdata.hasKit(gameType, "4")) {
			menu.setOption(15, new ItemStack(Material.ENCHANTED_BOOK), MessageUtils.getTranslation("editor.menu-load-kit").replace("$kit", "4"), "");
			menu.setOption(24, new ItemStack(Material.FIRE), MessageUtils.getTranslation("editor.menu-delete-kit").replace("$kit", "4"), "");
		} else {
			menu.setOption(15, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
			menu.setOption(24, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
		}

		if(pdata.hasKit(gameType, "5")) {
			menu.setOption(17, new ItemStack(Material.ENCHANTED_BOOK), MessageUtils.getTranslation("editor.menu-load-kit").replace("$kit", "5"), "");
			menu.setOption(26, new ItemStack(Material.FIRE), MessageUtils.getTranslation("editor.menu-delete-kit").replace("$kit", "5"), "");
		} else {
			menu.setOption(17, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
			menu.setOption(26, new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7), MessageUtils.getTranslation("editor.menu-unavailable-kit"));
		}

		return menu;
	}

	public boolean isEditing(Player player) {
		return editingList.containsKey(player);
	}
	
	public void cleanPlayer(Player player) {
		player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
		player.getInventory().setContents(InventoryFactory.getDefaultInventory(player));
		player.updateInventory();

		this.editingList.remove(player);
		this.renaming.remove(player);

		PlayerData data = Practice.getInstance().getDataManager().getData(player);

		if(data.isHidingPlayers()) {
			return;
		}

		EntityHider hider = Practice.getInstance().getEntityHider();

		for(Player p : Bukkit.getOnlinePlayers()) {
			hider.showEntity(player, p);
			hider.showEntity(p, player);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(this.editingList.containsKey(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(this.editingList.containsKey(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		if(this.editingList.containsKey(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if(this.editingList.containsKey(event.getPlayer())) event.setCancelled(true);
	}

	@EventHandler
	public void onLaunch(ProjectileLaunchEvent event) {
		if(!(event.getEntity().getShooter() instanceof Player)) return;

		Player player = (Player) event.getEntity().getShooter();

		if(this.editingList.containsKey(player)) event.setCancelled(true);
	}

	@EventHandler
	public void onRightClickSign(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) && editingList.containsKey(event.getPlayer())) {
			PlayerUtils.prepareForSpawn(event.getPlayer());

			event.getPlayer().sendMessage("");
			event.getPlayer().sendMessage(MessageUtils.getTranslation("editor.finished-editing"));
			event.getPlayer().sendMessage("");
		}
	}

	@EventHandler
	public void onClickAnvil(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(!editingList.containsKey(player)) return;
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(event.getClickedBlock() == null) return;

		if(event.getClickedBlock().getType() == Material.ANVIL) {
			getOptionMenu(player, this.editingList.get(player)).open(player);
			event.setCancelled(true);
			return;
		}
	}

}