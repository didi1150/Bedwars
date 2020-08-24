package me.didi.utils.countdowns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.didi.BWMain;
import me.didi.utils.GameManager;
import me.didi.utils.GameTeam;
import me.didi.utils.shop.BedwarsShop;

public class EndingCountDown extends Countdown
{
	public static int COUNTDOWN_TIME = 15;
	private int seconds;
	private BWMain plugin;

	public EndingCountDown(BWMain plugin)
	{
		this.seconds = COUNTDOWN_TIME;
		this.plugin = plugin;
	}

	@Override
	public void start()
	{
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{

			@Override
			public void run()
			{

				switch (seconds)
				{
					case 10:
					case 5:
					case 4:
					case 3:
					case 2:
						Bukkit.broadcastMessage(BWMain.prefix + ChatColor.GOLD + "Der Server startet neu in "
								+ ChatColor.GREEN + seconds + ChatColor.GOLD + " Sekunden");
						break;

					case 1:
						Bukkit.broadcastMessage(BWMain.prefix + ChatColor.GOLD + "Der Server startet neu in "
								+ ChatColor.GREEN + "einer" + ChatColor.GOLD + " Sekunde");
						break;
					case 0:
						plugin.getGameStateManager().getCurrentGameState().stop();
						break;

					default:
						break;
				}
				seconds--;
			}
		}, 20, 20);
	}

	@Override
	public void stop()
	{
		Bukkit.getScheduler().cancelTask(taskID);
		GameManager gameManager = plugin.getGameManager();
		for (GameTeam team : gameManager.getAliveTeams())
		{
			new BedwarsShop(team, plugin, "Shop", gameManager.getTeamShop(plugin.getVoting().getWinnerMap(), team))
					.destroy();
		}
	}

}
