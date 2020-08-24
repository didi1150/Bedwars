package me.didi.utils;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Bed;

import me.didi.BWMain;
import me.didi.utils.voting.Map;

public class GameTeam
{
	private String name, prefix;
	private ArrayList<String> members;
	private byte colorData;
	private int maxPlayers;
	BWMain plugin = BWMain.getInstance();
	public static final String TEAM_INVENTORY_NAME = ChatColor.GOLD + "Wähle dein Team!";
	private Location bedHeadLocation;
	private BlockFace bedFacing;
	GameManager gameManager = plugin.getGameManager();
	private Location spawn;

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
		return plugin.getConfig().getString("Teams." + getName() + ".prefix");
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

	public void create()
	{
		plugin.getConfig().set("Teams." + name + ".name", name.toUpperCase());
		plugin.getConfig().set("Teams." + getName().toUpperCase() + ".prefix", prefix);
		plugin.getConfig().set("Teams." + getName().toUpperCase() + ".colordata", colorData);
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

	public void saveSpawn(Map map, Location spawn)
	{
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

	public void setSpawn(Location loc)
	{
		this.spawn = loc;
	}

	public boolean bedExists()
	{
		return bedHeadLocation.getBlock().getType() != Material.AIR;
	}

	public String bedPlaced()
	{
		return bedExists() ? "§a✓" : "§c✘";
	}

	public void placeBed()
	{
		Block bedHeadBlock = bedHeadLocation.getBlock();
		Block bedFootBlock = bedHeadBlock.getRelative(bedFacing.getOppositeFace());

		BlockState bedFootState = bedFootBlock.getState();
		bedFootState.setType(Material.BED_BLOCK);
		Bed bedFootData = new Bed(Material.BED_BLOCK);
		bedFootData.setHeadOfBed(false);
		bedFootData.setFacingDirection(bedFacing);
		bedFootState.setData(bedFootData);
		bedFootState.update(true);

		BlockState bedHeadState = bedHeadBlock.getState();
		bedHeadState.setType(Material.BED_BLOCK);
		Bed bedHeadData = new Bed(Material.BED_BLOCK);
		bedHeadData.setHeadOfBed(true);
		bedHeadData.setFacingDirection(bedFacing);
		bedHeadState.setData(bedHeadData);
		bedHeadState.update(true);
	}

	public void setBedHeadLocation(Location bedHeadLocation)
	{
		this.bedHeadLocation = bedHeadLocation;
	}

	public Location getBedBottom()
	{
		/*
		 * Block bedHeadBlock =
		 * gameManager.getLocation(plugin.getVoting().getWinnerMap(), "teams." +
		 * getName() + ".bed") .getBlock(); Block bedFootBlock =
		 * bedHeadBlock.getRelative(
		 * BlockFace.valueOf(plugin.getConfig().getString("teams." + getName() +
		 * ".bed.face")).getOppositeFace()); return bedFootBlock.getLocation();
		 */
		Block bedBottomBlock = bedHeadLocation.getBlock().getRelative(bedFacing.getOppositeFace());
		return bedBottomBlock.getLocation();
	}

	public Location getBedTop()
	{
		return bedHeadLocation;
		// return gameManager.getLocation(plugin.getVoting().getWinnerMap(), "teams." +
		// getName() + ".bed");
	}

	public void setBedFacing(BlockFace bedFacing)
	{
		this.bedFacing = bedFacing;
	}

	public void addMember(Player p)
	{
		GameManager gameManager = plugin.getGameManager();
		if (!hasMember(p))
		{
			for (GameTeam team : gameManager.getTeams())
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

	public Location getForge()
	{
		GameManager gameManager = new GameManager(plugin);
		return gameManager.getLocation(plugin.getVoting().getWinnerMap(), "teams." + getName() + ".spawner");
	}
}
