package me.didi.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.didi.BWMain;
import me.didi.utils.voting.Map;

public class Utils
{

	private static BWMain plugin;
	private static ArrayList<GameTeam> teams;
	private int[] teamsOrder = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
	private static int taskID;

	public Utils(BWMain plugin)
	{
		Utils.plugin = plugin;
		teams = new ArrayList<GameTeam>();

		initTeams();
	}

	public static GameTeam getTeam(String name)
	{
		GameTeam team = new GameTeam(name.toUpperCase(),
				plugin.getConfig().getString("Teams." + name.toUpperCase() + ".prefix"),
				(byte) (plugin.getConfig().getInt("Teams." + name.toUpperCase() + ".colordata")));
		return team;
	}

	public static ArrayList<GameTeam> getTeams()
	{
		return teams;
	}

	private void initTeams()
	{
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("Teams");
		if (section != null)
		{
			for (String key : section.getKeys(false))
			{
				GameTeam team = new GameTeam(section.getString(key + ".name"),
						section.getString(key + ".prefix").replaceAll("&", "§"),
						(byte) (section.getInt(key + ".colordata")));
				team.setMaxPlayers(plugin.getMaxplayerperteam());
				if (!teams.contains(team))
				{
					teams.add(team);
				}
			}
		}
	}

	public static void removeMember(Player p, GameTeam t)
	{
		if (t.hasMember(p))
			t.getMembers().remove(p.getName());
	}

	public int[] getTeamsOrder()
	{
		return teamsOrder;
	}

	public static void addToRandomTeam(Player p)
	{
		for (GameTeam team : teams)
		{
			if (!team.hasMember(p))
			{
				team.addMember(p);
				break;
			}
		}
	}

	public static void setBlockLocation(Map map, Location loc, String name)
	{
		plugin.getConfig().set("Maps." + map.getName() + "." + name + ".x", loc.getBlockX() + 0.5);
		plugin.getConfig().set("Maps." + map.getName() + "." + name + ".y", loc.getBlockY());
		plugin.getConfig().set("Maps." + map.getName() + "." + name + ".z", loc.getBlockZ() + 0.5);
		plugin.getConfig().set("Maps." + map.getName() + "." + name + ".world", loc.getWorld().getName());
		plugin.saveConfig();
	}

	public static void spawnItems(final Map map, final World w)
	{
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			int delay = 0;
			Item item;

			public void run()
			{
				delay++;
				w.setThundering(false);
				w.setStorm(false);
				w.setTime(6000);
				if ((delay == 15) || (delay == 30))
				{
					for (int i = 1; i <= plugin.getConfig().getInt("Locations." + map.getName() + ".GoldCount"); i++)
					{
						if (plugin.getConfig().contains("Maps." + map.getName() + ".GOLDSPAWNER-" + i))
							item = w.dropItem(getBlockLocation(map, "GOLDSPAWNER-" + i),
									new ItemStack(Material.GOLD_INGOT));
						item.setVelocity(new Vector(0, 0, 0));
					}
				}
				if (delay == 30)
				{
					for (int i = 1; i <= plugin.getConfig().getInt("Locations." + map.getName() + ".EmeraldCount"); i++)
					{
						if (plugin.getConfig().contains("Maps." + map.getName() + ".EMERALDSPAWNER-" + i))
							item = w.dropItem(getBlockLocation(map, "EMERALDSPAWNER-" + i),
									new ItemStack(Material.EMERALD));
						item.setVelocity(new Vector(0, 0, 0));
					}
				}
				for (int i = 1; i <= plugin.getConfig().getInt("Locations." + map.getName() + ".IronCount"); i++)
				{
					if (plugin.getConfig().contains("Maps." + map.getName() + ".IRONSPAWNER-" + i))
						item = w.dropItem(getBlockLocation(map, "IRONSPAWNER-" + i),
								new ItemStack(Material.IRON_INGOT));
					item.setVelocity(new Vector(0, 0, 0));
				}

			}
		}, 10, 10);
	}

	public static Location getBlockLocation(Map map, String name)
	{
		String mainPath = "Maps." + map.getName() + "." + name;

		return (new Location(Bukkit.getWorld(plugin.getConfig().getString(mainPath + ".world")),
				plugin.getConfig().getDouble(mainPath + ".x"), plugin.getConfig().getDouble(mainPath + ".y"),
				plugin.getConfig().getDouble(mainPath + ".z")));
	}
}