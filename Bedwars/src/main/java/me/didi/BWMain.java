package me.didi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.didi.commands.BWCommand;
import me.didi.listener.PlayerListener;
import me.didi.utils.gamestates.GameState;
import me.didi.utils.gamestates.GameStateManager;
import me.didi.utils.voting.Map;
import me.didi.utils.voting.Voting;

public class BWMain extends JavaPlugin
{

	public static String prefix = "§a§lBedwars §8•§r§7 ";
	public File mysqlfile = new File("plugins/" + this.getName(), "mysql.yml");
	public File file = new File("plugins/" + this.getName(), "config.yml");
	FileConfiguration cfg = this.getConfig();
	FileConfiguration mysqlcfg = YamlConfiguration.loadConfiguration(mysqlfile);
	private static BWMain instance;
	public int min_players, max_players;
	private Voting voting;
	private ArrayList<Map> maps;
	private GameStateManager gameStateManager;
	private ArrayList<Player> players;

	@Override
	public void onEnable()
	{
		instance = this;
		addConfigValues();
		initFiles();
		initValues();
		initVoting();
		gameStateManager.setGameState(GameState.LOBBY_STATE);
		addMySQLDefaultValues();
		registerCommands();
		registerEvents();

	}

	private void registerEvents()
	{
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerListener(instance), instance);
	}

	public void addMySQLDefaultValues()
	{
		mysqlcfg.options().copyDefaults(true);
		mysqlcfg.addDefault("username", "root");
		mysqlcfg.addDefault("password", "");
		mysqlcfg.addDefault("host", "localhost");
		mysqlcfg.addDefault("database", "Bedwars");
		saveCfg(mysqlcfg, mysqlfile);
	}

	public void addConfigValues()
	{
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("finished", false);
		getConfig().addDefault("maxplayer", 1);
		getConfig().addDefault("minplayer", 0);
		saveConfig();
	}

	private void initVoting()
	{
		maps = new ArrayList<Map>();
		for (String current : getConfig().getConfigurationSection("Maps").getKeys(false))
		{
			Bukkit.getConsoleSender().sendMessage(prefix + "forSchleife!");
			Map map = new Map(this, current.toUpperCase());
			if (map.playable())
			{
				maps.add(map);
				Bukkit.getConsoleSender().sendMessage(prefix + "§cEine Map wurde geaddet!");
			} else
			{
				Bukkit.getConsoleSender().sendMessage(
						BWMain.prefix + "§4Die Map " + map.getName() + " §cmuss noch eingerichtet werden!");
			}
		}
		if (maps.size() >= Voting.MAP_AMOUNT)
		{
			voting = new Voting(this, maps);
		}
	}

	public void initFiles()
	{
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}

			if (!getDataFolder().exists())
			{
				getDataFolder().mkdir();
			}

			if (!mysqlfile.exists())
			{
				mysqlfile.createNewFile();
			}
		} catch (IOException e)
		{
			return;
		}
	}

	public void saveCfg(FileConfiguration cfg, File file)
	{
		try
		{
			cfg.save(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static BWMain getInstance()
	{
		return instance;
	}

	@Override
	public void onDisable()
	{

	}

	private void initValues()
	{
		min_players = getConfig().getInt("minplayer");
		max_players = getConfig().getInt("maxplayer");
		gameStateManager = new GameStateManager(instance);
		players = new ArrayList<Player>();
	}

	public void registerCommands()
	{
		getCommand("bw").setExecutor(new BWCommand());
	}

	public boolean isFinished()
	{
		return getConfig().getBoolean("finished");
	}

	public Voting getVoting()
	{
		return voting;
	}

	public ArrayList<Map> getMaps()
	{
		return maps;
	}

	public GameStateManager getGameStateManager()
	{
		return gameStateManager;
	}
	
	public ArrayList<Player> getPlayers()
	{
		return players;
	}
}
