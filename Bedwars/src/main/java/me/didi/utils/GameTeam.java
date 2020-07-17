package me.didi.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.didi.BWMain;
import me.didi.utils.voting.Map;

public class GameTeam
{
	private String name, prefix;
	private ArrayList<String> players;
	private byte colorData;
	private int maxPlayers;
	private Location spawn;
	static BWMain plugin = BWMain.getInstance();

	public GameTeam(Map map, String name, String prefix, byte colorData)
	{
		this.name = name.toUpperCase();
		this.prefix = prefix;
		this.colorData = colorData;
		this.players = new ArrayList<String>();
		this.maxPlayers = BWMain.getInstance().getConfig().getInt("Maps." + map.getName() + ".maxplayerperteam");
	}

	public String getName()
	{
		return name;
	}

	public int getMaxPlayers()
	{
		return maxPlayers;
	}

	public static ArrayList<GameTeam> getTeams(Map map)
	{
		ArrayList<GameTeam> teams = new ArrayList<GameTeam>();
		for (String key : plugin.getConfig().getConfigurationSection("Maps." + map.getName().toUpperCase() + ".teams")
				.getKeys(false))
		{
			GameTeam team = new GameTeam(map, plugin.getConfig().getString(key),
					plugin.getConfig().getString(key + ".prefix"),
					(byte) (plugin.getConfig().getInt(key + ".colordata")));
			teams.add(team);
		}

		return teams;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public ArrayList<String> getPlayers()
	{
		return players;
	}

	public boolean isMember(Player p)
	{
		return players.contains(p.getDisplayName());
	}

	public ItemStack getIcon()
	{
		ItemStack is = new ItemStack(Material.WOOL, 1, colorData);
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(prefix + name);
		meta.setLore(players);
		is.setItemMeta(meta);

		return is;
	}

	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}

	public void addTeam(Map map)
	{
		plugin.getConfig().set("Maps." + map.getName() + ".teams." + name + ".name", name.toUpperCase());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + getName().toUpperCase() + ".prefix",
				getPrefix());
		plugin.getConfig().set(
				"Maps." + map.getName().toUpperCase() + ".teams." + getName().toUpperCase() + ".colordata",
				getColorData());
		plugin.saveCfg(plugin.getConfig(), plugin.file);
	}

	public byte getColorData()
	{
		return colorData;
	}

	public boolean exists(Map map)
	{
		return (plugin.getConfig()
				.get("Maps." + map.getName().toUpperCase() + ".teams." + this.getName().toUpperCase()) != null);
	}

	public void delete(Map map)
	{
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + getName().toUpperCase(), null);
		plugin.saveConfig();
	}

	public void setSpawn(Map map, Location spawn)
	{
		this.spawn = spawn;
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + name + ".spawn.x", spawn.getX());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + name + ".spawn.y", spawn.getY());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + name + ".spawn.z", spawn.getZ());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + name + ".spawn.yaw", spawn.getYaw());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + name + ".spawn.pitch",
				spawn.getPitch());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + name + ".spawn.world",
				spawn.getWorld().getName());
		plugin.saveConfig();
	}
}
