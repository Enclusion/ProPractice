package net.propvp.practice.scoreboard;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.game.GameType;
import net.propvp.practice.game.matchmaking.MatchMaker;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.player.PlayerElo;
import net.propvp.practice.utils.MessageUtils;

public class ScoreboardManager extends BukkitRunnable {

	private FileConfiguration config;
	private ConfigurationSection section;
	private String separator;
	private String gamemodeFormat;
	private String ratingFormat;
	private String enderpearlFormat;
	private List<String> idleLines;
	private List<String> matchLines;
	private List<String> queueLines;

	@SuppressWarnings("unchecked")
	public ScoreboardManager() {
		this.config = PracticeConfiguration.getRootConfig().getConfig();

		if(this.config.getConfigurationSection("scoreboard") == null) {
			new Exception("Failed to find scoreboard configuration secion.");
		}

		this.section = this.config.getConfigurationSection("scoreboard");
		this.separator = MessageUtils.color(this.section.getString("separator"));
		this.gamemodeFormat = this.section.getString("gamemode");
		this.ratingFormat = this.section.getString("rating");
		this.enderpearlFormat = MessageUtils.color(this.section.getString("enderpearl"));
		this.idleLines = (List<String>) this.section.getList("idle-lines");
		this.matchLines = (List<String>) this.section.getList("match-lines");
		this.queueLines = (List<String>) this.section.getList("queue-lines");
	}

	public void run() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			PlayerData data = Practice.getInstance().getDataManager().getData(player);
			ScoreboardHelper board = data.getScoreboardHelper();
			ScoreboardUser user = data.getScoreboardUser();
			board.clear();

			if(data.isHidingScoreboard()) {
				board.update(player);
				return;
			}

			if(!data.inMatchmaking() && data.getMatch() == null) {
				if(!idleLines.isEmpty()) {
					for(String line : idleLines) {
						if(line.equalsIgnoreCase("$separator")) {
							board.add(separator, separator);
							continue;
						}
						
						if(line.equalsIgnoreCase("$ratings")) {
							if(data.getRatings().isEmpty()) {
								board.add("&7No data found", "");
							} else {
								for(Entry<GameType, PlayerElo> entry : data.getRatings().entrySet()) {
									String left = gamemodeFormat.replace("$gamemode", entry.getKey().getName());
									String right = ratingFormat.replace("$rating", entry.getValue().getRating() + "");

									board.add(MessageUtils.color(left), MessageUtils.color(right));
								}
							}

							continue;
						}
						
						line = line.replace("$usersOnline", "" + Bukkit.getOnlinePlayers().size());
						line = line.replace("$playerName", player.getName());
						line = MessageUtils.color(line);
						
						if(line.length() > 16) {
							if(ChatColor.getLastColors(TextSplitter.getSecondSplit(line).substring(0, 2)) == "") {
								board.add(TextSplitter.getFirstSplit(line), ChatColor.getLastColors(TextSplitter.getFirstSplit(line)) + TextSplitter.getSecondSplit(line));
							} else {
								board.add(TextSplitter.getFirstSplit(line), TextSplitter.getSecondSplit(line));
							}
						} else {
							board.add(TextSplitter.getFirstSplit(line), TextSplitter.getSecondSplit(line));
						}
					}
				}
			}

			if(data.inMatchmaking()) {
				if(!queueLines.isEmpty()) {
					for(String line : queueLines) {
						if(line.equalsIgnoreCase("$separator")) {
							board.add(separator, separator);
							continue;
						}

						MatchMaker matchMaker = data.getMatchMaker();

						line = line.replace("$time", matchMaker.getSearchTimer(data.inParty() ? data.getParty() : player).toString());
						line = line.replace("$queue", matchMaker.getName());
						line = line.replace("$usersOnline", "" + Bukkit.getOnlinePlayers().size());
						line = line.replace("$playerName", player.getName());

						if(matchMaker.isRanked()) {
							line = line.replace("$ranges", (data.getRating(matchMaker.getGameType()).getRating() - matchMaker.getSearchRange(data.inParty() ? data.getParty() : player)) + " -> " + (data.getRating(matchMaker.getGameType()).getRating() + matchMaker.getSearchRange(data.inParty() ? data.getParty() : player)));
						} else {
							line = line.replace("$ranges", "Not ranked");
						}
						
						line = MessageUtils.color(line);
						
						if(line.length() > 16) {
							if(ChatColor.getLastColors(TextSplitter.getSecondSplit(line).substring(0, 2)) == "") {
								board.add(TextSplitter.getFirstSplit(line), ChatColor.getLastColors(TextSplitter.getFirstSplit(line)) + TextSplitter.getSecondSplit(line));
							} else {
								board.add(TextSplitter.getFirstSplit(line), TextSplitter.getSecondSplit(line));
							}
						} else {
							board.add(TextSplitter.getFirstSplit(line), TextSplitter.getSecondSplit(line));
						}
					}
				}
			}

			if(data.inMatch()) {
				if(!matchLines.isEmpty()) {
					for(String line : matchLines) {
						if(line.equalsIgnoreCase("$separator")) {
							board.add(separator, separator);
							continue;
						}

						line = line.replace("$time", data.getMatch().getTimer().toString());
						line = line.replace("$rival", data.getMatch().getRival(data.getMatch().getTeamOfPlayer(player)).getName());
						line = line.replace("$usersOnline", "" + Bukkit.getOnlinePlayers().size());
						line = line.replace("$playerName", player.getName());
						line = MessageUtils.color(line);

						if(line.length() > 16) {
							if(ChatColor.getLastColors(TextSplitter.getSecondSplit(line).substring(0, 2)) == "") {
								board.add(TextSplitter.getFirstSplit(line), ChatColor.getLastColors(TextSplitter.getFirstSplit(line)) + TextSplitter.getSecondSplit(line));
							} else {
								board.add(TextSplitter.getFirstSplit(line), TextSplitter.getSecondSplit(line));
							}
						} else {
							board.add(TextSplitter.getFirstSplit(line), TextSplitter.getSecondSplit(line));
						}
					}
				}
			}

			if(user.hasAnyActiveTimers()) {
				if(user.getEnderpearlTimer().isActive()) {
					DecimalFormat decimalFormat = new DecimalFormat("##.#");
					String line = enderpearlFormat;

					if(line.length() > 32) {
						line = line.substring(0, 32);
					}

					line = line.replace("$time", decimalFormat.format(((double)user.getEnderpearlTimer().getTimeLeft() / 1000)));
					line = MessageUtils.color(line);
					
					board.add(TextSplitter.getFirstSplit(line), TextSplitter.getSecondSplit(line));
					board.add(separator, separator);
				}
			}

			board.update(player);
		}
	}
}
