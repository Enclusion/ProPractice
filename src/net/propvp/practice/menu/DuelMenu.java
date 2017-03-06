package net.propvp.practice.menu;

import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.game.GameType;
import net.propvp.practice.game.duel.DuelInfo;
import net.propvp.practice.game.matchmaking.MatchMaker;
import net.propvp.practice.game.matchmaking.NormalMatchMaker;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.utils.IconMenu;
import net.propvp.practice.utils.MessageUtils;

public class DuelMenu {

	public static void init(Player sender, Player target) {
		IconMenu menu = new IconMenu(MessageUtils.color(PracticeConfiguration.getRootConfig().getConfig().getString("menus.dueling")), 18, event -> {
			if(event.getPlayer() != event.getOwner()) return;
			if(target == null) throw new NullPointerException();

            Player p = event.getPlayer();
	        PlayerData senderData = Practice.getInstance().getDataManager().getData(p);
	        PlayerData targetData = Practice.getInstance().getDataManager().getData(target);
	        
	        if(senderData.inMatchmaking()) {
	        	p.sendMessage(ChatColor.RED + "You cannot use that menu while in matchmaking.");
	        	return;
	        }

            if(event.getName() != null) {
                p.closeInventory();

				if(senderData.inParty() != targetData.inParty()) {
					sender.sendMessage(ChatColor.RED + (senderData.inParty() ? "You can't invite someone to a duel if you're in a party and they're not." : "You can't invite a party to a duel if you're not in a party."));
					return;
				}

				GameType gameType = Practice.getInstance().getGameManager().getGameType(ChatColor.stripColor(event.getItemStack().getItemMeta().getDisplayName()));
				DuelInfo duelInfo = new DuelInfo(sender, target, gameType, senderData.inParty());
				targetData.addDuelInvite(sender, duelInfo);
                
                event.setWillClose(true);
                event.setWillDestroy(true);
            }
	    }, sender);
		
		int i = 0;
        
		for(Entry<UUID, MatchMaker> entry : Practice.getInstance().getGameManager().getMatchMakers().entrySet()) {
			if(!(entry.getValue() instanceof NormalMatchMaker)) { continue; }
			if(entry.getValue().isRanked()) { continue; }
			if(entry.getValue().isParty()) { continue; }
			if(entry.getValue().isFrozen()) { continue; }
			
			GameType gt = entry.getValue().getGameType();
			menu.setOption(i, gt.getIcon(), MessageUtils.color(gt.getName()), ChatColor.GRAY + "Click to select the game type.");
			i++;
		};
		
		menu.setDestroyOnClose(true);
		menu.open(sender);
	}
	
}