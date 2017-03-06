package net.propvp.practice.menu;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.game.GameType;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.IconMenu;
import net.propvp.practice.utils.MessageUtils;

public class EditorMenu {

	public static void openMenu(Player player) {
		IconMenu menu = new IconMenu(MessageUtils.color(PracticeConfiguration.getRootConfig().getConfig().getString("menus.kit-editing")), Practice.getInstance().getGameManager().getGameMenuAmount(), event -> {
			PlayerData data = Practice.getInstance().getDataManager().getData(player);

			if(data.inMatchmaking()) {
				player.sendMessage(MessageUtils.getTranslation("errors.menu-matchmaking"));
				return;
			}

			if(event.getName() != null) {
				player.closeInventory();
				if(!Practice.getInstance().getGameManager().gameTypeExists(event.getItemStack().getItemMeta().getDisplayName())) return;
				
				GameType gameType = Practice.getInstance().getGameManager().getGameType(event.getItemStack().getItemMeta().getDisplayName());
			    Practice.getInstance().getEditorManager().beginEditing(event.getPlayer(), gameType);
			}
		}, player);
		
		for(Entry<String, GameType> entry : Practice.getInstance().getGameManager().getGameTypes().entrySet()) {
			if(!entry.getValue().isEditable()) { continue; }

			GameType gt = entry.getValue();
			menu.setOption(gt.getDisplayOrder(), new ItemStack(gt.getIcon().getType(), 1, gt.getIcon().getDurability()), gt.getDisplayName(), ChatColor.GRAY + "Click to edit your kits", ChatColor.GRAY + "for this game type.");
		};
		
		menu.setDestroyOnClose(true);
		menu.open(player);
	}

}