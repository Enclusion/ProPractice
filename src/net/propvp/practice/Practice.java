package net.propvp.practice;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.propvp.practice.command.SimpleCommand;
import net.propvp.practice.command.type.*;
import net.propvp.practice.game.Game;
import net.propvp.practice.game.manager.ArenaManager;
import net.propvp.practice.game.manager.GameManager;
import net.propvp.practice.game.matchmaking.MatchMaker;
import net.propvp.practice.listener.*;
import net.propvp.practice.party.PartyManager;
import net.propvp.practice.player.DataManager;
import net.propvp.practice.player.EditorManager;
import net.propvp.practice.scoreboard.ScoreboardManager;
import net.propvp.practice.utils.EntityHider;
import net.propvp.practice.utils.InventoryFactory;

public class Practice extends JavaPlugin {

	private static Practice instance;

	private DataManager dataManager;
	private EditorManager editorManager;
	private PartyManager partyManager;
	private GameManager gameManager;
	private ArenaManager arenaManager;
	private EntityHider entityHider;
	private PracticeConfiguration configuration;
	
	private Map<String, SimpleCommand> COMMANDS;

	public void onEnable() {
		instance = this;
		
		this.COMMANDS = new HashMap<String, SimpleCommand>() {
			private static final long serialVersionUID = 1L;

			{
				this.put("dbflush", new DatabaseFlushCommand(Practice.this));
				this.put("practice", new PracticeCommand(Practice.this));
				this.put("arena", new ArenaCommand(Practice.this));
				this.put("duel", new DuelCommand(Practice.this));
				this.put("elo", new EloCommand(Practice.this));
				this.put("gametype", new GameTypeCommand(Practice.this));
				this.put("gapple", new GoldenHeadCommand(Practice.this));
				this.put("inventory", new InventoryCommand(Practice.this));
				this.put("party", new PartyCommand(Practice.this));
				this.put("ping", new PingCommand(Practice.this));
				this.put("scoreboard", new ScoreboardCommand(Practice.this));
				this.put("spectate", new SpectateCommand(Practice.this));
				this.put("ppv", new VersionCommand(Practice.this));
			}
		};

		loadConfig();
		loadCommands();
		loadListeners();

		gameManager = new GameManager();
		partyManager = new PartyManager();
		arenaManager = new ArenaManager();
		
		PracticeConfiguration.loadBackend();
		PracticeConfiguration.getStorage().createTables();

		dataManager = new DataManager();
		editorManager = new EditorManager();
		entityHider = new EntityHider(Practice.getInstance(), EntityHider.Policy.BLACKLIST);

		for(Player p : Bukkit.getOnlinePlayers()) {
			entityHider.showAllPlayers(p);
			PracticeConfiguration.teleportToSpawn(p);
			p.getInventory().clear();
			p.getInventory().setArmorContents(InventoryFactory.getEmptyArmor());
			p.getInventory().setContents(InventoryFactory.getDefaultInventory(p));
			p.updateInventory();
		}

		new ScoreboardManager().runTaskTimer(Practice.getInstance(), 1L, 1L);
	}

	public void onDisable() {
		if(this.gameManager.getMatchMakers().isEmpty()) return;
		
		for(MatchMaker matchMaker : this.gameManager.getMatchMakers().values()) {
			if(matchMaker.getMatches().isEmpty()) continue;
			
			for(Game game : matchMaker.getMatches()) {
				game.cancelMatch("The match has been cancelled because the server is being reloaded.");
			}
		}
	}

	private void loadConfig() {
		this.configuration = new PracticeConfiguration();
	}

	private void loadCommands() {
		this.getLogger().info("Initializing commands...");

		this.COMMANDS.forEach((name, command) -> {
			this.getCommand(name).setExecutor(command);
		});

		this.getLogger().info("Initialized commands.");
	}

	private void loadListeners() {
		this.getLogger().info("Initializing listeners...");
		this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PartyListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new RegionSelectionListener(), this);
		this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
		this.getServer().getPluginManager().registerEvents(new MiscListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		this.getLogger().info("Initialized listeners.");
	}

	public static Logger logger() {
		return Bukkit.getLogger();
	}

	public static Practice getInstance() {
		return instance;
	}

	public PracticeConfiguration getConfiguration() {
		return configuration;
	}
	
	public void shutdown() {
		this.getServer().getPluginManager().disablePlugin(this);
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}

	public DataManager getDataManager() {
		return dataManager;
	}
	
	public EditorManager getEditorManager() {
		return editorManager;
	}

	public PartyManager getPartyManager() {
		return partyManager;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public EntityHider getEntityHider() {
		return entityHider;
	}

}