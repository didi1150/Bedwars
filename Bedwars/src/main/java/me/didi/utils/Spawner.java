package me.didi.utils;

import java.util.ArrayList;

import org.bukkit.Location;

import me.didi.BWMain;
import me.didi.utils.voting.Map;

public class Spawner
{
	private Location location;
	private int delay;
	private BWMain plugin = BWMain.getInstance();

	public Spawner(Location loc, int delay)
	{
		this.location = loc;
		this.delay = delay;

	}

	public Location getLocation()
	{
		return location;
	}

}
