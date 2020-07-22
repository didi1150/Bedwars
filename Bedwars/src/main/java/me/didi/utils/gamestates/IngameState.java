package me.didi.utils.gamestates;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.didi.BWMain;
import me.didi.utils.GameTeam;
import me.didi.utils.Utils;

public class IngameState extends GameState
{

	BWMain plugin = BWMain.getInstance();

	@Override
	public void start()
	{
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
		{

			public void run()
			{
				for (GameTeam team : Utils.getTeams())
				{
					World w = team.getSpawn().getWorld();
					Utils.spawnItems(plugin.getVoting().getWinnerMap(), w);
				}
			}
		}, 20);

	}

	@Override
	public void stop()
	{

	}

}
