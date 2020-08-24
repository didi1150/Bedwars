package me.didi.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.didi.BWMain;
import me.didi.commands.subcommands.ShopCmd;
import me.didi.utils.gamestates.GameState;
import me.didi.utils.gamestates.GameStateManager;
import me.didi.utils.gamestates.IngameState;
import me.didi.utils.shop.BedwarsShop;
import me.didi.utils.voting.Map;
import me.didi.utils.voting.Voting;

public class GameManager
{

	private BWMain plugin;
	private ArrayList<GameTeam> teams = new ArrayList<GameTeam>();
	private List<Map> maps = new ArrayList<Map>();
	private int[] teamsOrder = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
	private int taskID;
	private Voting voting;
	private GameStateManager gameStateManager;
	private String[] colors = new String[] { "AQUA", "BLACK", "BLUE", "DARK_GRAY", "DARK_GREEN", "GREEN", "VIOLETT",
			"ORANGE", "PINK", "RED", "WHITE", "YELLOW", "GRAY", "DARK_RED", "DARK_BLUE" };
	private ArrayList<Location> buildedBlocks = new ArrayList<Location>();
	private int delay = 0;
	private int aliveTeamCount;
	private List<GameTeam> aliveTeams;

	public GameManager(BWMain plugin)
	{
		this.plugin = plugin;
		gameStateManager = plugin.getGameStateManager();
		aliveTeamCount = 0;
		aliveTeams = new ArrayList<>();

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
				GameTeam team = new GameTeam(section.getString(key + ".name"), section.getString(key + ".prefix"),
						(byte) (section.getInt(key + ".colordata")));
				team.setMaxPlayers(plugin.getMaxplayerperteam());
				if (!teams.contains(team))
				{
					teams.add(team);
				}
			}
		}

		ConfigurationSection mapSection = plugin.getConfig().getConfigurationSection("Maps");
		if (mapSection != null)
		{
			for (String key : mapSection.getKeys(false))
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
		for (GameTeam team : getTeams())
		{

			if (!team.hasMember(p))
			{
				if (team.getMembers().size() <= team.getMaxPlayers())
				{
					team.addMember(p);
					break;
				}
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

	public Location getLocation(Map map, String name)
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
		cfg.set("Maps." + map.getName() + "." + name + ".world", location.getWorld().getName());
		plugin.saveConfig();
	}

	public void setTeamShop(Map map, GameTeam team, Player player)
	{
		setLocation(map, player.getLocation(), "teams." + team.getName() + ".shop");
		plugin.saveConfig();
	}

	public Location getTeamShop(Map map, GameTeam team)
	{
		FileConfiguration cfg = plugin.getConfig();
		String mainPath = "Maps." + map.getName() + ".teams." + team.getName() + ".shop";
		return new Location(Bukkit.getWorld(cfg.getString(mainPath + ".world")), cfg.getDouble(mainPath + ".x"),
				cfg.getDouble(mainPath + ".y"), cfg.getDouble(mainPath + ".z"));
	}

	public void setLobby(Location location)
	{
		FileConfiguration cfg = plugin.getConfig();
		cfg.set("Lobby.x", location.getX());
		cfg.set("Lobby.y", location.getY());
		cfg.set("Lobby.z", location.getZ());
		cfg.set("Lobby.yaw", location.getYaw());
		cfg.set("Lobby.pitch", location.getPitch());
		cfg.set("Lobby.world", location.getWorld().getName());
		plugin.saveConfig();
	}

	public Location getLobby()
	{
		FileConfiguration cfg = plugin.getConfig();
		try
		{
			double x = cfg.getDouble("Lobby.x");
			double y = cfg.getDouble("Lobby.y");
			double z = cfg.getDouble("Lobby.z");
			double yaw = cfg.getDouble("Lobby.yaw");
			double pitch = cfg.getDouble("Lobby.pitch");
			World w = Bukkit.getWorld(cfg.getString("Lobby.world"));
			Location loc = new Location(w, x, y, z);
			loc.setYaw((float) yaw);
			loc.setPitch((float) pitch);
			return loc;
		} catch (NullPointerException e)
		{
			return null;
		}
	}

	public void startGame()
	{
		final Map winnerMap = voting.getWinnerMap();
		for (int i = 0; i < getTeams().size(); i++)
		{
			if (!getTeams().get(i).isEmpty())
			{
				aliveTeams.add(getTeams().get(i));
			}
			if (plugin.getConfig().contains("Maps." + winnerMap.getName() + ".teams." + getTeams().get(i).getName()))
			{
				for (String name : getTeams().get(i).getMembers())
				{
					Player p = Bukkit.getPlayer(name);
					p.teleport(getTeams().get(i).getSpawn());
					p.setHealth(20);
					p.setFoodLevel(20);
					p.setLevel(0);
					p.getInventory().clear();
					p.getActivePotionEffects().clear();
					p.getInventory().setArmorContents(null);
				}

				final World w = getTeams().get(1).getSpawn().getWorld();
				if (!getTeams().get(i).isEmpty())
				{
					getTeams().get(i).setBedFacing(BlockFace.valueOf(plugin.getConfig().getString(
							"Maps." + winnerMap.getName() + ".teams." + getTeams().get(i).getName() + ".bed.face")));
					getTeams().get(i).setBedHeadLocation(
							getLocation(winnerMap, "teams." + getTeams().get(i).getName() + ".bed"));
					getTeams().get(i).placeBed();

				}

				gameStateManager.setGameState(GameState.INGAME_STATE);
				for (GameTeam team : getAliveTeams())
				{
					new BedwarsShop(team, plugin, "Shop", getTeamShop(winnerMap, team)).spawn();
				}
				if (gameStateManager.getCurrentGameState() instanceof IngameState)
				{

					taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
					{

						public void run()
						{
							for (GameTeam team : getTeams())
							{
								if (delay % 16 == 0)
								{
									Item item = w.dropItem(team.getForge(), new ItemStack(Material.GOLD_INGOT));
									item.setVelocity(new Vector(0, 0, 0));
								}
								if (delay % 4 == 0)
								{
									Item item = w.dropItem(team.getForge(), new ItemStack(Material.IRON_INGOT));
									item.setVelocity(new Vector(0, 0, 0));
								}
							}
							delay++;
						}
					}, 20, 20);
				} else
					Bukkit.getScheduler().cancelTask(taskID);

			}
		}
	}

	public void addMap(Map map, String builder)
	{
		if (!map.exists())
			map.create(builder);
	}

	public List<Map> getMaps()
	{
		return maps;
	}

	public String[] getColors()
	{
		return colors;
	}

	public String getClosestFace(Player player)
	{

		double rotation = (player.getLocation().getYaw() - 180) % 360;
		if (rotation < 0)
		{
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5)
		{
			return "NORTH";
		} else if (22.5 <= rotation && rotation < 67.5)
		{
			return "NORTH-EAST";
		} else if (67.5 <= rotation && rotation < 112.5)
		{
			return "EAST";
		} else if (112.5 <= rotation && rotation < 157.5)
		{
			return "SOUTH-EAST";
		} else if (157.5 <= rotation && rotation < 202.5)
		{
			return "SOUTH";
		} else if (202.5 <= rotation && rotation < 247.5)
		{
			return "SOUTH-WEST";
		} else if (247.5 <= rotation && rotation < 292.5)
			return "WEST";

		return null;
	}

	public void setVoting(Voting voting)
	{
		this.voting = voting;
	}

	public ArrayList<Location> getBuildedBlocks()
	{
		return buildedBlocks;
	}

	public void setI(int i)
	{
		this.delay = i;
	}

	public int getAliveTeamCount()
	{
		aliveTeamCount = 0;
		for (GameTeam team : getTeams())
		{
			if (!team.isEmpty())
			{
				aliveTeamCount++;
			}
		}
		return aliveTeamCount;
	}

	public GameTeam getLastTeam()
	{
		if (getAliveTeamCount() == 1)
		{
			for (GameTeam team : getTeams())
			{
				if (!team.isEmpty())
				{
					return team;
				}
			}
		}
		return null;
	}

	public List<GameTeam> getAliveTeams()
	{
		return aliveTeams;
	}

	public GameTeam getTeamByPlayer(Player p)
	{
		for (GameTeam team : getAliveTeams())
		{
			if (team.hasMember(p))
			{
				return team;
			}
		}
		return null;
	}

}