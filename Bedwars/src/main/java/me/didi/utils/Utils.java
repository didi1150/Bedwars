package me.didi.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
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
import me.didi.utils.gamestates.IngameState;
import me.didi.utils.voting.Map;
import me.didi.utils.voting.Voting;

public class Utils
{

	private static BWMain plugin;
	private static ArrayList<GameTeam> teams;
	private int[] teamsOrder = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
	private static int taskID;
	private static Voting voting;
	private static GameStateManager gameStateManager;

	public Utils(BWMain plugin)
	{
		Utils.plugin = plugin;
		teams = new ArrayList<GameTeam>();
		gameStateManager = plugin.getGameStateManager();

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

	public static Location getBlockLocation(Map map, String name)
	{
		String mainPath = "Maps." + map.getName() + "." + name;

		return (new Location(Bukkit.getWorld(plugin.getConfig().getString(mainPath + ".world")),
				plugin.getConfig().getDouble(mainPath + ".x"), plugin.getConfig().getDouble(mainPath + ".y"),
				plugin.getConfig().getDouble(mainPath + ".z")));
	}

	public static void setLocation(Map map, Location location, String name)
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

	public static void setTeamShop(Map map, GameTeam team, Player player)
	{
		setLocation(map, player.getLocation(), "teams." + team.getName() + ".shop");
		player.sendMessage(
				BWMain.prefix + "§aDu hast erfolgreich den Shop von Team" + team.getPrefix() + team.getName());
		plugin.saveConfig();
	}

	public static Location getTeamShop(Map map, GameTeam team)
	{
		FileConfiguration cfg = plugin.getConfig();
		String mainPath = "Maps." + map.getName() + ".teams." + team.getName() + ".shop.";
		return new Location(Bukkit.getWorld(cfg.getString(mainPath + "world")), cfg.getDouble(mainPath + "x"),
				cfg.getDouble(mainPath + "y"), cfg.getDouble(mainPath + "z"));
	}

	public static void startGame()
	{
		for (GameTeam team : Utils.getTeams())
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

			final World w = team.getSpawn().getWorld();
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
			{
				int delay = 0;

				public void run()
				{
					delay++;
					if (gameStateManager.getCurrentGameState() instanceof IngameState)
					{
						w.setThundering(false);
						w.setStorm(false);
						w.setTime(6000);
						if ((delay == 15) || (delay == 30))
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
						if (delay == 30)
						{
							for (int i = 1; i <= plugin.getConfig()
									.getInt("Locations." + winnerMap.getName() + ".EmeraldCount"); i++)
							{
								if (plugin.getConfig().contains("Maps." + winnerMap.getName() + ".EMERALDSPAWNER-" + i))
								{
									Item item = w.dropItem(getBlockLocation(winnerMap, "EMERALDSPAWNER-" + i),
											new ItemStack(Material.EMERALD));
									item.setVelocity(new Vector(0, 0, 0));
									break;
								}
							}
							delay = 0;
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
					{
						Bukkit.getScheduler().cancelTask(taskID);
					}

				}
			}, 20, 20);
		}
	}

	public void setVoting(Voting voting)
	{
		Utils.voting = voting;
	}
}