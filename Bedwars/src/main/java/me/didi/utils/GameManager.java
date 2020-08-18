package me.didi.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.didi.BWMain;
import me.didi.utils.gamestates.GameState;
import me.didi.utils.gamestates.GameStateManager;
import me.didi.utils.voting.Map;
import me.didi.utils.voting.Voting;

public class GameManager
{

	private BWMain plugin;
	private ArrayList<GameTeam> teams;
	private List<Map> maps;
	private int[] teamsOrder = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
	private int taskID;
	private Voting voting;
	private GameStateManager gameStateManager;
	private boolean running;
	private String[] colors = new String[] { "AQUA", "BLACK", "BLUE", "DARK_GRAY", "DARK_GREEN", "GREEN", "VIOLETT",
			"ORANGE", "PINK", "RED", "WHITE", "YELLOW", "GRAY", "DARK_RED", "DARK_BLUE" };

	public GameManager(BWMain plugin)
	{
		this.plugin = plugin;
		teams = new ArrayList<GameTeam>();
		maps = new ArrayList<Map>();
		gameStateManager = plugin.getGameStateManager();

		init();
	}

	public ChatColor getChatColor(String color)
	{
		if (color.equalsIgnoreCase("AQUA"))
			return ChatColor.AQUA;
		else if (color.equalsIgnoreCase("BLACK"))
			return ChatColor.BLACK;
		else if (color.equalsIgnoreCase("BLUE"))
			return ChatColor.BLUE;
		else if (color.equalsIgnoreCase("DARK_GRAY"))
			return ChatColor.DARK_GRAY;
		else if (color.equalsIgnoreCase("DARK_GREEN"))
			return ChatColor.DARK_GREEN;
		else if (color.equalsIgnoreCase("GREEN"))
			return ChatColor.GREEN;
		else if (color.equalsIgnoreCase("VIOLETT"))
			return ChatColor.DARK_PURPLE;
		else if (color.equalsIgnoreCase("ORANGE"))
			return ChatColor.GOLD;
		else if (color.equalsIgnoreCase("PINK"))
			return ChatColor.LIGHT_PURPLE;
		else if (color.equalsIgnoreCase("RED"))
			return ChatColor.RED;
		else if (color.equalsIgnoreCase("WHITE"))
			return ChatColor.WHITE;
		else if (color.equalsIgnoreCase("YELLOW"))
			return ChatColor.YELLOW;
		else if (color.equalsIgnoreCase("GRAY"))
			return ChatColor.GRAY;
		else if (color.equalsIgnoreCase("DARK_RED"))
			return ChatColor.DARK_RED;
		else if (color.equalsIgnoreCase("DARK_BLUE"))
			return ChatColor.DARK_BLUE;
		else
			return null;
	}

	public DyeColor getDyeColor(String color)
	{
		if (color.equalsIgnoreCase("AQUA"))
			return DyeColor.CYAN;
		else if (color.equalsIgnoreCase("BLACK"))
			return DyeColor.BLACK;
		else if (color.equalsIgnoreCase("BLUE"))
			return DyeColor.LIGHT_BLUE;
		else if (color.equalsIgnoreCase("DARK_GRAY"))
			return DyeColor.GRAY;
		else if (color.equalsIgnoreCase("DARK_GREEN"))
			return DyeColor.GREEN;
		else if (color.equalsIgnoreCase("GREEN"))
			return DyeColor.LIME;
		else if (color.equalsIgnoreCase("VIOLETT"))
			return DyeColor.PURPLE;
		else if (color.equalsIgnoreCase("ORANGE"))
			return DyeColor.ORANGE;
		else if (color.equalsIgnoreCase("PINK"))
			return DyeColor.MAGENTA;
		else if (color.equalsIgnoreCase("RED"))
			return DyeColor.RED;
		else if (color.equalsIgnoreCase("WHITE"))
			return DyeColor.WHITE;
		else if (color.equalsIgnoreCase("YELLOW"))
			return DyeColor.YELLOW;
		else if (color.equalsIgnoreCase("GRAY"))
			return DyeColor.SILVER;
		else if (color.equalsIgnoreCase("DARK_RED"))
			return DyeColor.BROWN;
		else if (color.equalsIgnoreCase("DARK_BLUE"))
			return DyeColor.BLUE;
		else
			return null;
	}

	public GameTeam getTeam(String name)
	{
		GameTeam team = new GameTeam(name.toUpperCase(),
				plugin.getConfig().getString("Teams." + name.toUpperCase() + ".prefix"),
				(byte) (plugin.getConfig().getInt("Teams." + name.toUpperCase() + ".colordata")));
		return team;
	}

	public ArrayList<GameTeam> getTeams()
	{
		return teams;
	}

	private void init()
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

		section = plugin.getConfig().getConfigurationSection("Maps");
		if (section != null)
		{
			for (String key : section.getKeys(false))
			{
				Map map = new Map(plugin, key);
				if (!maps.contains(map))
				{
					maps.add(map);
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

	public void addToRandomTeam(Player p)
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

	public void setBlockLocation(Map map, Location loc, String name)
	{
		plugin.getConfig().set("Maps." + map.getName() + "." + name + ".x", loc.getBlockX() + 0.5);
		plugin.getConfig().set("Maps." + map.getName() + "." + name + ".y", loc.getBlockY());
		plugin.getConfig().set("Maps." + map.getName() + "." + name + ".z", loc.getBlockZ() + 0.5);
		plugin.getConfig().set("Maps." + map.getName() + "." + name + ".world", loc.getWorld().getName());
		plugin.saveConfig();
	}

	public Location getBlockLocation(Map map, String name)
	{
		String mainPath = "Maps." + map.getName() + "." + name;

		return (new Location(Bukkit.getWorld(plugin.getConfig().getString(mainPath + ".world")),
				plugin.getConfig().getDouble(mainPath + ".x"), plugin.getConfig().getDouble(mainPath + ".y"),
				plugin.getConfig().getDouble(mainPath + ".z")));
	}

	public void setLocation(Map map, Location location, String name)
	{
		FileConfiguration cfg = plugin.getConfig();
		cfg.set("Maps." + map.getName() + "." + name + ".x", location.getX());
		cfg.set("Maps." + map.getName() + "." + name + ".y", location.getY());
		cfg.set("Maps." + map.getName() + "." + name + ".z", location.getZ());
		cfg.set("Maps." + map.getName() + "." + name + ".yaw", location.getYaw());
		cfg.set("Maps." + map.getName() + "." + name + ".pitch", location.getPitch());
		cfg.set("Maps." + map.getName() + "." + name + ".world", location.getWorld());
		plugin.saveConfig();
	}

	public void setTeamShop(Map map, GameTeam team, Player player)
	{
		setLocation(map, player.getLocation(), "teams." + team.getName() + ".shop");
		player.sendMessage(
				BWMain.prefix + "§aDu hast erfolgreich den Shop von Team" + team.getPrefix() + team.getName());
		plugin.saveConfig();
	}

	public Location getTeamShop(Map map, GameTeam team)
	{
		FileConfiguration cfg = plugin.getConfig();
		String mainPath = "Maps." + map.getName() + ".teams." + team.getName() + ".shop.";
		return new Location(Bukkit.getWorld(cfg.getString(mainPath + "world")), cfg.getDouble(mainPath + "x"),
				cfg.getDouble(mainPath + "y"), cfg.getDouble(mainPath + "z"));
	}

	public void startGame()
	{
		for (GameTeam team : getTeams())
		{
			final BWMain plugin = BWMain.getInstance();
			final Map winnerMap = voting.getWinnerMap();
			Location loc = new Location(
					Bukkit.getWorld(plugin.getConfig().getString(
							"Maps." + voting.getWinnerMap().getName() + ".teams." + team.getName() + ".spawn.world")),
					plugin.getConfig().getInt(
							"Maps." + voting.getWinnerMap().getName() + ".teams." + team.getName() + ".spawn.x"),
					plugin.getConfig().getInt(
							"Maps." + voting.getWinnerMap().getName() + ".teams." + team.getName() + ".spawn.y"),
					plugin.getConfig().getInt(
							"Maps." + voting.getWinnerMap().getName() + ".teams." + team.getName() + ".spawn.z"));
			loc.setPitch(plugin.getConfig()
					.getInt("Maps." + voting.getWinnerMap().getName() + ".teams." + team.getName() + ".spawn.pitch"));
			loc.setYaw(plugin.getConfig()
					.getInt("Maps." + voting.getWinnerMap().getName() + ".teams." + team.getName() + ".spawn.yaw"));
			team.setSpawn(voting.getWinnerMap(), loc);
			for (String name : team.getMembers())
			{
				Player p = Bukkit.getPlayer(name);
				p.teleport(team.getSpawn());
				p.setHealth(20);
				p.setFoodLevel(20);
				p.setLevel(0);
				p.getInventory().clear();
				p.getActivePotionEffects().clear();
				p.getInventory().setArmorContents(null);
			}

			if (!team.isEmpty())
			{
				// TODO : Bed placen
			}
			gameStateManager.setGameState(GameState.INGAME_STATE);
			running = true;

			final World w = team.getSpawn().getWorld();
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
			{
				int i = 0;

				public void run()
				{
					i++;
					if (running)
					{
						w.setThundering(false);
						w.setStorm(false);
						w.setTime(6000);
						if ((i == 15) || (i == 30))
						{
							for (int i = 1; i <= plugin.getConfig()
									.getInt("Locations." + winnerMap.getName() + ".GoldCount"); i++)
							{
								if (plugin.getConfig().contains("Maps." + winnerMap.getName() + ".GOLDSPAWNER-" + i))
								{
									Item item = w.dropItem(getBlockLocation(winnerMap, "GOLDSPAWNER-" + i),
											new ItemStack(Material.GOLD_INGOT));
									item.setVelocity(new Vector(0, 0, 0));
								}
							}
						}
						if (i == 30)
						{
							for (int i = 1; i <= plugin.getConfig()
									.getInt("Locations." + winnerMap.getName() + ".EmeraldCount"); i++)
							{
								if (plugin.getConfig().contains("Maps." + winnerMap.getName() + ".EMERALDSPAWNER-" + i))
								{
									Item item = w.dropItem(getBlockLocation(winnerMap, "EMERALDSPAWNER-" + i),
											new ItemStack(Material.EMERALD));
									item.setVelocity(new Vector(0, 0, 0));
								}
							}
							i = 0;
						}
						for (int i = 1; i <= plugin.getConfig()
								.getInt("Locations." + winnerMap.getName() + ".IronCount"); i++)
						{
							if (plugin.getConfig().contains("Maps." + winnerMap.getName() + ".IRONSPAWNER-" + i))
							{
								Item item = w.dropItem(getBlockLocation(winnerMap, "IRONSPAWNER-" + i),
										new ItemStack(Material.IRON_INGOT));
								item.setVelocity(new Vector(0, 0, 0));
							}
						}
					} else
						Bukkit.getScheduler().cancelTask(taskID);

				}
			}, 20, 20);
		}
	}

	public void addMap(Map map, String builder)
	{
		if (!map.exists())
			map.create(builder);
	}

	public void setVoting(Voting voting)
	{
		this.voting = voting;
	}

	public List<Map> getMaps()
	{
		return maps;
	}
	
	public String[] getColors()
	{
		return colors;
	}
}