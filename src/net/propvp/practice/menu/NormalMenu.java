package net.propvp.practice.menu;

import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.game.GameType;
import net.propvp.practice.game.matchmaking.MatchMaker;
import net.propvp.practice.game.matchmaking.NormalMatchMaker;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.HiddenStringUtil;
import net.propvp.practice.utils.IconMenu;
import net.propvp.practice.utils.MessageUtils;

public class NormalMenu {

	public static void openMenu(Player player) {
		PlayerData data = Practice.getInstance().getDataManager().getData(player);

		IconMenu menu = new IconMenu(MessageUtils.color(PracticeConfiguration.getRootConfig().getConfig().getString("menus.normal-matchmaking")), Practice.getInstance().getGameManager().getGameMenuAmount(), event -> {
			if(data.inMatchmaking()) {
				player.sendMessage(MessageUtils.getTranslation("errors.menu-matchmaking"));
				return;
			}

			if(event.getName() != null) {
				player.closeInventory();
				Practice.getInstance().getGameManager().addToMatchMaker(event.getUUID(), player);
			}
		}, player);
		
		for(Entry<UUID, MatchMaker> entry : Practice.getInstance().getGameManager().getMatchMakers().entrySet()) {
			if(!(entry.getValue() instanceof NormalMatchMaker)) { continue; }
			if(entry.getValue().isRanked()) { continue; }
			if(entry.getValue().isParty()) { continue; }
			if(entry.getValue().isFrozen()) { continue; }

			GameType gt = entry.getValue().getGameType();

			if(gt.getDisplayOrder() > 45 || gt.getDisplayOrder() <= -1) continue;

			menu.setOption(gt.getDisplayOrder(), new ItemStack(gt.getIcon().getType(), 1, gt.getIcon().getDurability()), gt.getDisplayName(), (MessageUtils.getTranslation("menus.searching").replace("$count", entry.getValue().getSearchingCount() + "")), (MessageUtils.getTranslation("menus.playing").replace("$count", entry.getValue().getPlayingCount() + "")), HiddenStringUtil.encodeString(entry.getValue().getIdentifier().toString()));
		};
		
		menu.setDestroyOnClose(true);
		menu.open(player);
	}

}