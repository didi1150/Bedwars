package me.didi.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.didi.BWMain;
import me.didi.utils.voting.Map;

public class GameTeam
{
	private String name, prefix;
	private ArrayList<String> members;
	private byte colorData;
	private int maxPlayers;
	private Location spawn;
	static BWMain plugin = BWMain.getInstance();
	public static final String TEAM_INVENTORY_NAME = "§6Wähle dein Team!";

	public GameTeam(String name, String prefix, byte colorData)
	{
		this.name = name.toUpperCase();
		this.prefix = prefix;
		this.colorData = colorData;
		this.members = new ArrayList<String>();
	}

	public String getName()
	{
		return name;
	}

	public int getMaxPlayers()
	{
		return maxPlayers;
	}

	public String getPrefix()
	{
		return prefix.replaceAll("&", "§");
	}

	public ArrayList<String> getMembers()
	{
		return members;
	}

	public boolean hasMember(Player p)
	{
		return members.contains(p.getName());
	}

	public ItemStack getIcon()
	{
		ItemStack is = new ItemStack(Material.WOOL, 1, colorData);
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(prefix + name);
		ArrayList<String> players = new ArrayList<String>();
		for (String player : members)
		{
			players.add("§7- " + getPrefix() + player);
		}
		meta.setLore(players);
		is.setItemMeta(meta);

		return is;
	}

	public boolean isEmpty()
	{
		return members.isEmpty();
	}

	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}

	public void addTeam()
	{
		plugin.getConfig().set("Teams." + name + ".name", name.toUpperCase());
		plugin.getConfig().set("Teams." + getName().toUpperCase() + ".prefix", getPrefix());
		plugin.getConfig().set("Teams." + getName().toUpperCase() + ".colordata", getColorData());
		plugin.saveCfg(plugin.getConfig(), plugin.file);
	}

	public byte getColorData()
	{
		return colorData;
	}

	public boolean exists()
	{
		return (plugin.getConfig().get("Teams." + this.getName().toUpperCase()) != null);
	}

	public void delete()
	{
		plugin.getConfig().set("Teams." + getName().toUpperCase(), null);
		plugin.saveConfig();
	}

	public void deleteSpawn(Map map)
	{
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".teams." + getName().toUpperCase() + ".spawn",
				null);
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

	public void addMember(Player p)
	{
		if (!hasMember(p))
		{
			for (GameTeam team : Utils.getTeams())
			{
				team.removeMember(p);
			}

			members.add(p.getName());
		} else
		{
			p.sendMessage(BWMain.prefix + "§cDu bist schon in diesem Team!");
		}
	}

	public void removeMember(Player p)
	{
		if (hasMember(p))
			members.remove(p.getName());
	}

	public Location getSpawn()
	{
		return spawn;
	}
}
