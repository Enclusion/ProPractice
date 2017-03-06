package net.propvp.practice.game;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import mkremins.fanciful.FancyMessage;
import net.propvp.practice.Practice;
import net.propvp.practice.PracticeConfiguration;
import net.propvp.practice.game.arena.Arena;
import net.propvp.practice.game.matchmaking.MatchMaker;
import net.propvp.practice.game.matchmaking.NormalMatchMaker;
import net.propvp.practice.game.matchmaking.RankedMatchMaker;
import net.propvp.practice.party.Party;
import net.propvp.practice.player.PlayerData;
import net.propvp.practice.player.PlayerElo;
import net.propvp.practice.timing.ElapsedTimer;
import net.propvp.practice.utils.EntityHider;
import net.propvp.practice.utils.InventoryFactory;
import net.propvp.practice.utils.MessageUtils;
import net.propvp.practice.utils.PlayerUtils;

public class Match implements Game, Listener {

	private Arena arena;
	private GameType gameType;
	private MatchMaker matchMaker;
	private List<Location> placedBlocks;
	private List<Player> specList;
	private ElapsedTimer matchTimer;
	private Team team1;
	private Team team2;
	private boolean hasStarted;
	private long startTime;
	private long endTime;

	public Match(Arena arena, GameType gameType, MatchMaker matchMaker, Object obj1, Object obj2) {
		this.arena = arena;
		this.gameType = gameType;
		this.matchMaker = matchMaker;
		this.team1 = new Team(obj1);
		this.team2 = new Team(obj2);
		this.placedBlocks = new ArrayList<Location>();
		this.specList = new ArrayList<Player>();
		this.matchTimer = new ElapsedTimer();
		this.hasStarted = false;
		this.startTime = System.currentTimeMillis();

		if(!arena.isSetup()) {
			cancelMatch(MessageUtils.getTranslation("errors.mismatch-match"));
			return;
		}

		if(matchMaker instanceof RankedMatchMaker) {
			if(team1.isParty() || team2.isParty()) {
				cancelMatch(MessageUtils.getTranslation("errors.mismatch-participant"));
				return;
			}

			if(!matchMaker.isRanked()) {
				cancelMatch(MessageUtils.getTranslation("errors.mismatch-match"));
				return;
			}
		}

		if(matchMaker instanceof NormalMatchMaker) {
			if((team1.isParty() && !team2.isParty()) || (!team1.isParty() && team2.isParty())) {
				cancelMatch(MessageUtils.getTranslation("errors.mismatch-participant"));
				return;
			}

			if((team1.getPlayers().size() > matchMaker.getMaximumPlayers()) || (team1.getPlayers().size() < matchMaker.getMinimumPlayers())) {
				cancelMatch(MessageUtils.getTranslation("errors.mismatch-participant"));
				return;
			}

			if((team2.getPlayers().size() > matchMaker.getMaximumPlayers()) || (team2.getPlayers().size() < matchMaker.getMinimumPlayers())) {
				cancelMatch(MessageUtils.getTranslation("errors.mismatch-participant"));
				return;
			}

			if(matchMaker.isRanked()) {
				cancelMatch(MessageUtils.getTranslation("errors.mismatch-match"));
				return;
			}
		}

		EntityHider hider = Practice.getInstance().getEntityHider();
		
		for(Player player : this.getPlayers()) {
			PlayerUtils.setDefaults(player);
			Practice.getInstance().getEntityHider().hideAllPlayers(player);
			Practice.getInstance().getDataManager().getData(player).setMatch(this);
		}

		for(Player player : team1.getPlayers().keySet()) {
			for(Player p : team1.getPlayers().keySet()) {
				hider.showEntity(player, p);
				hider.showEntity(p, player);
			}

			for(Player p : team2.getPlayers().keySet()) {
				hider.showEntity(player, p);
				hider.showEntity(p, player);
			}
		}

		startMatch();
		
		new BukkitRunnable() {
			public void run() {
				Bukkit.getPluginManager().registerEvents(Match.this, Practice.getInstance());
			}
		}.runTaskLater(Practice.getInstance(), 2L);
	}

	public Arena getArena() {
		return arena;
	}

	public GameType getGameType() {
		return gameType;
	}

	public MatchMaker getMatchMaker() {
		return matchMaker;
	}

	public Team getTeam1() {
		return team1;
	}

	public Team getTeam2() {
		return team2;
	}

	public boolean hasStarted() {
		return hasStarted;
	}

	public List<Player> getSpectators() {
		return specList;
	}

	public void addSpectator(Player player) {
		specList.add(player);
	}

	public void removeSpectator(Player player) {
		specList.remove(player);
	}
	
	public ElapsedTimer getTimer() {
		return matchTimer;
	}

	public void sendMessage(String msg) {
		for(Player player : team1.getPlayers().keySet()) {
			player.sendMessage(msg);
		}

		for(Player player : team2.getPlayers().keySet()) {
			player.sendMessage(msg);
		}
	}

	public void playSound(Sound sound, float idk, float idk2) {
		for(Player player : this.getPlayers()) {
			if(Practice.getInstance().getDataManager().getData(player).isHidingSounds()) continue;
			player.playSound(player.getLocation(), sound, idk, idk2);
		}
	}

	public void startMatch() {
		for(Player player : this.getPlayers()) {
			Practice.getInstance().getDataManager().getData(player).showKits(player, gameType);
			player.setMaximumNoDamageTicks(gameType.getHitDelay());
		}
		
		for(Player player : team1.getPlayers().keySet()) {
			player.teleport(arena.getSpawn1());
			player.sendMessage(MessageUtils.getTranslation("match.starting-against").replace("$rival", team2.getName()));
		}

		for(Player player : team2.getPlayers().keySet()) {
			player.teleport(arena.getSpawn2());
			player.sendMessage(MessageUtils.getTranslation("match.starting-against").replace("$rival", team1.getName()));
		}

		new BukkitRunnable() {
			private int i = 5;

			public void run() {
				if(this.i <= 0) {
					this.cancel();
					Match.this.startTime = System.currentTimeMillis();
					Match.this.hasStarted = true;
					Match.this.matchTimer.reset();

					sendMessage(MessageUtils.getTranslation("match.started-match"));
					playSound(Sound.NOTE_PIANO, 10.0f, 2.0f);
					return;
				} else {
					if(Match.this.matchTimer == null) {
						this.cancel();
						return;
					}

					Match.this.matchTimer.reset();

					sendMessage(MessageUtils.getTranslation("match.starting-time").replace("$time", String.valueOf(i)));
					playSound(Sound.NOTE_PIANO, 10.0f, 1.0f);
				}

				--this.i;
			}
		}.runTaskTimer(Practice.getInstance(), 0L, 20L);
	}

	public void endMatch(Team winner) {
		Team loser = getRival(winner);

		this.hasStarted = false;
		this.endTime = System.currentTimeMillis();
		double elapsedTime = (double) TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);

		for(Entry<Player, Boolean> player : team1.getPlayers().entrySet()) {
			if(player == null) continue;

			if(player.getValue()) {
				InventoryFactory.storeInv(player.getKey(), false);
			}

			PlayerData data = Practice.getInstance().getDataManager().getData(player.getKey());
			data.getScoreboardUser().getEnderpearlTimer().reset();
			data.setMatch(null);

			PracticeConfiguration.teleportToSpawn(player.getKey());
			player.getKey().sendMessage(MessageUtils.getTranslation("match.finished-time").replace("$time", String.valueOf(elapsedTime)));
			player.getKey().sendMessage(MessageUtils.getTranslation("match.finished-winner").replace("$winner", winner.getName()));
			player.getKey().sendMessage(MessageUtils.getTranslation("match.finished-loser").replace("$loser", loser.getName()));
			player.getKey().sendMessage(MessageUtils.getTranslation("match.finished-inventories"));

			for(Player p : team1.getPlayers().keySet()) {
				new FancyMessage(" » ").color(ChatColor.GRAY).then(p.getName()).color(ChatColor.YELLOW).command("/inventory " + p.getName()).send(player.getKey());
			}

			for(Player p : team2.getPlayers().keySet()) {
				new FancyMessage(" » ").color(ChatColor.GRAY).then(p.getName()).color(ChatColor.YELLOW).command("/inventory " + p.getName()).send(player.getKey());
			}
		}

		for(Entry<Player, Boolean> player : team2.getPlayers().entrySet()) {
			if(player == null) continue;

			if(player.getValue()) {
				InventoryFactory.storeInv(player.getKey(), false);
			}

			PlayerData data = Practice.getInstance().getDataManager().getData(player.getKey());
			data.getScoreboardUser().getEnderpearlTimer().reset();
			data.setMatch(null);

			PracticeConfiguration.teleportToSpawn(player.getKey());
			player.getKey().sendMessage(MessageUtils.getTranslation("match.finished-time").replace("$time", String.valueOf(elapsedTime)));
			player.getKey().sendMessage(MessageUtils.getTranslation("match.finished-winner").replace("$winner", winner.getName()));
			player.getKey().sendMessage(MessageUtils.getTranslation("match.finished-loser").replace("$loser", loser.getName()));
			player.getKey().sendMessage(MessageUtils.getTranslation("match.finished-inventories"));

			for(Player p : team1.getPlayers().keySet()) {
				new FancyMessage(" » ").color(ChatColor.GRAY).then(p.getName()).color(ChatColor.YELLOW).command("/inventory " + p.getName()).send(player.getKey());
			}

			for(Player p : team2.getPlayers().keySet()) {
				new FancyMessage(" » ").color(ChatColor.GRAY).then(p.getName()).color(ChatColor.YELLOW).command("/inventory " + p.getName()).send(player.getKey());
			}
		}

		if(matchMaker instanceof RankedMatchMaker) {
			Player winnerP = (Player) winner.getPlayers().keySet().toArray()[0];
			Player loserP = (Player) loser.getPlayers().keySet().toArray()[0];
			
			PlayerData winnerD = Practice.getInstance().getDataManager().getData(winnerP);
			PlayerData loserD = Practice.getInstance().getDataManager().getData(loserP);
			
			if(PracticeConfiguration.isLimitedMatches() && !winnerP.hasPermission("practice.limited-matches.bypass") && !winnerP.isOp()) {
				winnerD.decrementLimitedMatches();
				
				if(winnerD.getLimitedMatchesRemaining() <= 0) {
					winnerP.sendMessage(MessageUtils.getTranslation("limited-matches.match-limit-reached"));
				} else {
					winnerP.sendMessage(MessageUtils.getTranslation("limited-matches.match-limit-notification").replace("$amountRemaining", winnerD.getLimitedMatchesRemaining() + ""));
				}
			}

			if(PracticeConfiguration.isLimitedMatches() && !loserP.hasPermission("practice.limited-matches.bypass") && !loserP.isOp()) {
				loserD.decrementLimitedMatches();
				
				if(loserD.getLimitedMatchesRemaining() == 0) {
					loserP.sendMessage(MessageUtils.getTranslation("limited-matches.match-limit-reached"));
				} else {
					loserP.sendMessage(MessageUtils.getTranslation("limited-matches.match-limit-notification").replace("$amountRemaining", loserD.getLimitedMatchesRemaining() + ""));
				}
			}

			PlayerElo winnerR = winnerD.getRating(gameType);
			PlayerElo loserR = loserD.getRating(gameType);

			int winnerChange = (int) winnerR.newRatingWin(loserR) - winnerR.getRating();
			int loserChange = (int) loserR.newRatingLoss(winnerR) - loserR.getRating();

			winnerR.setRating(winnerR.getRating() + winnerChange);
			loserR.setRating(loserR.getRating() + loserChange);

			winnerD.saveAccount();
			loserD.saveAccount();

			PracticeConfiguration.getStorage().insertMatch(winnerD.getPlayer(), loserD.getPlayer(), winnerChange, loserChange, elapsedTime);

			for(Player player : new Player[] {winnerP, loserP}) {
				if(player == null) continue;
				
				player.sendMessage(MessageUtils.getTranslation("match.finished-changes").replace("$winnerChange", String.valueOf((int)Math.round(winnerChange))).replace("$loserChange", String.valueOf((int)Math.round(loserChange))).replace("$winner", winnerD.getPlayer().getName()).replace("$loser", loserD.getPlayer().getName()));
				player.getInventory().setContents(InventoryFactory.getDefaultInventory(player));
				player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
				player.updateInventory();
			}
		} else {
			if(matchMaker.isParty()) {
				for(Player player : getPlayers()) {
					if(player == null) continue;
					
					for(PotionEffect effect : player.getActivePotionEffects()) {
						player.removePotionEffect(effect.getType());
					}
					
					player.getInventory().setContents(InventoryFactory.getMemberInventory());
					player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
					player.updateInventory();
				}
				
				((Party)winner.getObject()).getLeader().getInventory().setContents(InventoryFactory.getLeaderInventory());
				((Party)winner.getObject()).getLeader().getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
				((Party)winner.getObject()).getLeader().updateInventory();
			} else {
				for(Player player : getPlayers()) {
					for(PotionEffect effect : player.getActivePotionEffects()) {
						player.removePotionEffect(effect.getType());
					}
					
					player.getInventory().setContents(InventoryFactory.getDefaultInventory(player));
					player.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
					player.updateInventory();
				}
			}
		}

		if(!specList.isEmpty()) {
			for(Player player : specList) {
				if(player == null) continue;
				PlayerUtils.prepareForSpawn(player);
				player.sendMessage(MessageUtils.getTranslation("match.finished-viewers"));
			}
		}

		cleanBlocks();
		garbageCollector();
	}

	public void cancelMatch(String reason) {
		for(Player player : team1.getPlayers().keySet()) {
			if(player == null) continue;
			Practice.getInstance().getDataManager().getData(player).setMatch(null);
			PlayerUtils.prepareForSpawn(player);
			player.sendMessage(ChatColor.RED + reason);
		}

		for(Player player : team2.getPlayers().keySet()) {
			if(player == null) continue;
			Practice.getInstance().getDataManager().getData(player).setMatch(null);
			PlayerUtils.prepareForSpawn(player);
			player.sendMessage(ChatColor.RED + reason);
		}

		if(!specList.isEmpty()) {
			for(Player player : specList) {
				if(player == null) continue;
				Practice.getInstance().getDataManager().getData(player).setSpectating(false);
				PlayerUtils.prepareForSpawn(player);
				player.sendMessage(ChatColor.RED + reason);
			}
		}
		
		cleanBlocks();
		garbageCollector();
	}
	
	public long getStartTime() {
		return startTime;
	}

	public Team getRival(Team team) {
		if(team1.equals(team)) {
			return team2;
		} else if(team2.equals(team)) {
			return team1;
		} else {
			return null;
		}
	}

	public Team getTeamOfPlayer(Player player) {
		if(team1.getPlayers().containsKey(player)) {
			return team1;
		}

		if(team2.getPlayers().containsKey(player)) {
			return team2;
		}

		return null;
	}

	public boolean containsPlayer(Player player) {
		return (team1.getPlayers().containsKey(player) || team2.getPlayers().containsKey(player));
	}
	
	public void cleanBlocks() {
		if(!placedBlocks.isEmpty()) {
			Iterator<Location> iterator = placedBlocks.iterator();
			
			while(iterator.hasNext()) {
				Location loc = iterator.next();
				arena.getSpawn1().getWorld().getBlockAt(loc).setType(Material.AIR);
				iterator.remove();
			}
		}
		
		this.arena.setActive(false);
	}

	public void garbageCollector() {
		this.matchMaker.removeMatch(this);
		this.arena = null;
		this.gameType = null;
		this.specList = null;
		this.matchTimer = null;
		this.team1 = null;
		this.team2 = null;

		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onFluidMove(BlockFromToEvent event) {
		if(!this.arena.getCuboid().containsLocation(event.getBlock().getLocation())) { Bukkit.getLogger().severe("test 1"); return; }
		this.placedBlocks.add(event.getToBlock().getLocation());
		Bukkit.getLogger().severe("FromTo: " + event.getToBlock().getLocation().toString());
	}

	@EventHandler
	public void onKit(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if(!containsPlayer(player)) return;
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		if(event.getItem() == null) return;
		
		if(event.getItem().getType() == Material.ENCHANTED_BOOK && ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).startsWith("Default")) {
			String[] split = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).split(" ");

			if(Practice.getInstance().getGameManager().gameTypeExists(split[1])) {
				player.getInventory().setContents(Practice.getInstance().getGameManager().getGameType(split[1]).getInventory().getContents());
				player.getInventory().setArmorContents(Practice.getInstance().getGameManager().getGameType(split[1]).getInventory().getArmorContents());
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if(!containsPlayer(player)) return;
		if(!arena.getCuboid().containsLocation(player.getLocation())) event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!containsPlayer(event.getPlayer())) return;
		if(!hasStarted) { event.setCancelled(true); return; }
		if(!gameType.isBuilding()) { event.setCancelled(true); return; }
		if(!arena.getCuboid().containsLocation(event.getBlock().getLocation())) { event.setCancelled(true); return; }
		if(!event.isCancelled()) placedBlocks.add(event.getBlockPlaced().getLocation());
	}

	@EventHandler
	public void onBlockDestroy(BlockBreakEvent event) {
		if(!containsPlayer(event.getPlayer())) return;
		if(!hasStarted) { event.setCancelled(true); return; }
		if(!gameType.isBreaking()) { event.setCancelled(true); return; }
		if(arena.isSafeBlock(event.getBlock().getLocation())) { event.setCancelled(true); return; }
		if(!arena.getCuboid().containsLocation(event.getBlock().getLocation())) event.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if(!containsPlayer(event.getPlayer())) return;
		if(!hasStarted) { event.setCancelled(true); return; }
		if(!gameType.isBuilding()) { event.setCancelled(true); return; }
		if(arena.isSafeBlock(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) { event.setCancelled(true); return; }
		if(!arena.getCuboid().containsLocation(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) { event.setCancelled(true); return; }
		if(!event.isCancelled()) placedBlocks.add(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		if(!containsPlayer(event.getPlayer())) return;
		if(!hasStarted) { event.setCancelled(true); return; }
		if(!gameType.isBreaking()) { event.setCancelled(true); return; }
		if(arena.isSafeBlock(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) { event.setCancelled(true); return; }
		if(!arena.getCuboid().containsLocation(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation())) event.setCancelled(true);
	}
	
	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		
		if(!containsPlayer(player)) return;
		if(!event.getItem().getItemMeta().hasDisplayName()) return;
		
		if(event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 240, 0));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!containsPlayer((Player) event.getEntity())) return;
		if(!gameType.isHunger()) event.setFoodLevel(20);
	}

	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(!containsPlayer((Player) event.getEntity())) return;

		if(!gameType.isRegeneration()) {
			if(event.getRegainReason() == RegainReason.SATIATED) event.setCancelled(true);
			if(event.getRegainReason() == RegainReason.REGEN) event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamageEntity(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if(!containsPlayer(player)) return;
		if(!hasStarted) { event.setCancelled(true); return; }

		if(player.getHealth() - event.getFinalDamage() <= 0) {
			List<DamageCause> nonEtityCauses = Arrays.asList(DamageCause.POISON, DamageCause.THORNS, DamageCause.DROWNING, DamageCause.FALL, DamageCause.FALLING_BLOCK, DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA, DamageCause.LIGHTNING, DamageCause.STARVATION, DamageCause.SUFFOCATION, DamageCause.VOID, DamageCause.WITHER, DamageCause.SUICIDE);
			if(!nonEtityCauses.contains(event.getCause())) return;
			
			sendMessage(MessageUtils.getTranslation("match.player-suicide").replace("$player", player.getName()));
			InventoryFactory.storeInv(player, true);
			PlayerUtils.prepareForSpectator(player);
			player.setHealth(20);

			for(Player p : team1.getPlayers().keySet()) {
				Practice.getInstance().getEntityHider().showEntity(player, p);
			}

			for(Player p : team2.getPlayers().keySet()) {
				Practice.getInstance().getEntityHider().showEntity(player, p);
			}

			for(Player p : specList) {
				Practice.getInstance().getEntityHider().showFadedEntity(player, p);
				Practice.getInstance().getEntityHider().showFadedEntity(p, player);
			}

			if((team1.getPlayers().containsKey(player) && team1.amountLeft() - 1 == 0)) {
				endMatch(team2);
			} else if((team2.getPlayers().containsKey(player) && team2.amountLeft() - 1 == 0)) {
				endMatch(team1);
			} else {
				PlayerUtils.prepareForSpectator(player);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if(!containsPlayer(player)) return;
		if(!hasStarted) { event.setCancelled(true); return; }

		if(event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();

			if(arrow.getShooter() instanceof Player) {
				Player damager = (Player) arrow.getShooter();

				double healthDisplay;

				if(player.getHealth() - event.getFinalDamage() <= 0) {
					healthDisplay = 0.0;
				} else {
					healthDisplay = (player.getHealth() - event.getFinalDamage()) / 2;
				}

				NumberFormat formatter = new DecimalFormat("#0.0");
				String newFormat = formatter.format(healthDisplay);

				damager.sendMessage(MessageUtils.getTranslation("match.bow-indicator").replace("$player", player.getName()).replace("$health", newFormat).replace("$heartEmoji", StringEscapeUtils.unescapeJava("\u2764")));
			}
		}

		if(player.getHealth() - event.getFinalDamage() <= 0) {
			InventoryFactory.storeInv(player, true);
			PlayerUtils.prepareForSpectator(player);
			player.setHealth(20);

			for(Player p : team1.getPlayers().keySet()) {
				Practice.getInstance().getEntityHider().showEntity(player, p);
			}

			for(Player p : team2.getPlayers().keySet()) {
				Practice.getInstance().getEntityHider().showEntity(player, p);
			}

			for(Player p : specList) {
				Practice.getInstance().getEntityHider().showFadedEntity(player, p);
				Practice.getInstance().getEntityHider().showFadedEntity(p, player);
			}

			if(event.getDamager() instanceof Arrow) {
				player.teleport((Player)((Arrow)event.getDamager()).getShooter());
				sendMessage(MessageUtils.getTranslation("match.player-slain").replace("$player", player.getName()).replace("$killer", ((Player)((Arrow)event.getDamager()).getShooter()).getName()));
			} else if(event.getDamager() instanceof Player) {
				player.teleport((Player)event.getDamager());
				sendMessage(MessageUtils.getTranslation("match.player-slain").replace("$player", player.getName()).replace("$killer", ((Player)event.getDamager()).getName()));
			} else {
				sendMessage(MessageUtils.getTranslation("match.player-suicide").replace("$player", player.getName()));
			}

			if((team1.getPlayers().containsKey(player) && team1.amountLeft() - 1 == 0)) {
				endMatch(team2);
			} else if((team2.getPlayers().containsKey(player) && team2.amountLeft() - 1 == 0)) {
				endMatch(team1);
			} else {
				PlayerUtils.prepareForSpectator(player);
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		if(!containsPlayer(player)) return;

		new BukkitRunnable() {
			public void run() {
				event.getItemDrop().remove();
			}
		}.runTaskLater(Practice.getInstance(), 100L);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if(!containsPlayer(player)) return;
		
		player.spigot().respawn();

		event.getDrops().clear();
		event.setDeathMessage(null);
		
		if((team1.getPlayers().containsKey(player) && team1.amountLeft() - 1 == 0)) {
			endMatch(team2);
		} else if((team2.getPlayers().containsKey(player) && team2.amountLeft() - 1== 0)) {
			endMatch(team1);
		} else {
			PlayerUtils.prepareForSpectator(player);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(!containsPlayer(player)) return;

		if((team1.getPlayers().containsKey(player) && team1.amountLeft() - 1 == 0)) {
			endMatch(team2);
		} else if((team2.getPlayers().containsKey(player) && team2.amountLeft() - 1== 0)) {
			endMatch(team1);
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		if(!containsPlayer(player)) return;

		if((team1.getPlayers().containsKey(player) && team1.amountLeft() - 1 == 0)) {
			endMatch(team2);
		} else if((team2.getPlayers().containsKey(player) && team2.amountLeft() - 1== 0)) {
			endMatch(team1);
		}
	}

	@Override
	public List<Player> getPlayers() {
		if(team1 == null || team2 == null) return null;
		
		List<Player> newList = new ArrayList<Player>();
		team1.getPlayers().keySet().forEach(player -> newList.add(player));
		team2.getPlayers().keySet().forEach(player -> newList.add(player));
		return newList;
	}

}