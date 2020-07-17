package me.didi.utils.voting;

import org.bukkit.Location;

import me.didi.BWMain;

public class Map
{

	private BWMain plugin;
	private String name;
	private Location spectatorSpawnLocation;
	private String builder;

	public Map(BWMain plugin, String name)
	{
		this.plugin = plugin;
		this.name = name.toUpperCase();
	}

	public boolean exists()
	{
		return (plugin.getConfig().get("Maps." + name + ".Builder") != null);
	}

	public void create(String builder)
	{
		this.builder = builder;
		plugin.getConfig().set("Maps." + this.name + ".Builder", this.builder);
		plugin.saveCfg(plugin.getConfig(), plugin.file);
	}

	public void delete()
	{
		plugin.getConfig().set("Maps." + this.name, null);
	}

	public String getName()
	{
		return name;
	}

	public Location getSpectatorSpawnLocation()
	{
		return spectatorSpawnLocation;
	}

	public void setSpectatorSpawnLocation(Location spectatorSpawnLocation)
	{
		this.spectatorSpawnLocation = spectatorSpawnLocation;
		plugin.getConfig().set("Maps." + name + ".spectatorspawn.x", spectatorSpawnLocation.getX());
		plugin.getConfig().set("Maps." + name + ".spectatorspawn.y", spectatorSpawnLocation.getY());
		plugin.getConfig().set("Maps." + name + ".spectatorspawn.z", spectatorSpawnLocation.getZ());
		plugin.getConfig().set("Maps." + name + ".spectatorspawn.yaw", spectatorSpawnLocation.getYaw());
		plugin.getConfig().set("Maps." + name + ".spectatorspawn.pitch", spectatorSpawnLocation.getPitch());
		plugin.getConfig().set("Maps." + name + ".spectatorspawn.world", spectatorSpawnLocation.getWorld().getName());
		plugin.saveConfig();
	}

	public String getBuilder()
	{
		return this.builder;
	}

}
