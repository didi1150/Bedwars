package me.didi.utils;

import org.bukkit.Location;

import me.didi.BWMain;
import me.didi.utils.voting.Map;

public class Spawner
{
	private Location loc;
	private int delay;
	private SpawnCategory spawnItem;
	private int ID;
	private Map map;
	private BWMain plugin = BWMain.getInstance();

	public Spawner(Map map, Location loc, int delay, SpawnCategory spawnItem, int ID)
	{
		this.map = map;
		this.loc = loc;
		this.delay = delay;
		this.spawnItem = spawnItem;
	}

	public void addSpawner(Map map)
	{
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".spawners.id", ID);
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".spawners.id.location.x." + ID, loc.getX());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".spawners.id.location.y." + ID, loc.getY());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".spawners.id.location.z." + ID, loc.getZ());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".spawners.id.itemType." + ID,
				spawnItem.toString());
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".spawners.id.delay." + ID, delay);
		plugin.getConfig().set("Maps." + map.getName().toUpperCase() + ".spawners.id.world." + ID,
				loc.getWorld().getName());
		plugin.saveConfig();
	}

	public boolean exists()
	{
		return plugin.getConfig().get("Maps." + map.getName().toUpperCase() + ".spawners.id" + "") != null;
	}

}
