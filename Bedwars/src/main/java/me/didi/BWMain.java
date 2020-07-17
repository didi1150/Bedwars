package me.didi;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.didi.commands.BWCommand;
import me.didi.utils.GameState;

public class BWMain extends JavaPlugin
{

	public static String prefix = "§a§lBedwars §8•§r§7 ";
	public File mysqlfile = new File("plugins/" + this.getName(), "mysql.yml");
	public File file = new File("plugins/" + this.getName(), "config.yml");
	FileConfiguration cfg = this.getConfig();
	FileConfiguration mysqlcfg = YamlConfiguration.loadConfiguration(mysqlfile);
	private static BWMain instance;
	public int min_players, max_players;

	@Override
	public void onEnable()
	{
		instance = this;
		GameState.setCurrentState(GameState.LOBBY);
		initFiles();
		addMySQLDefaultValues();
		registerCommands();
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
		saveConfig();
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

	public void registerCommands()
	{
		getCommand("bw").setExecutor(new BWCommand());
	}

	public boolean isFinished()
	{
		return getConfig().getBoolean("finished");
	}
}
