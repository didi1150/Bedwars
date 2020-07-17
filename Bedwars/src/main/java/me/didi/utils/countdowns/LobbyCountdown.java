package me.didi.utils.countdowns;

import org.bukkit.Bukkit;

import me.didi.BWMain;
import me.didi.utils.GameState;

public class LobbyCountdown extends Countdown
{

	private int idleID;
	private boolean isIdling;
	private boolean isRunning;
	private int seconds;

	BWMain plugin = BWMain.getInstance();

	@Override
	public void start()
	{
		isRunning = true;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{

			public void run()
			{
				switch (seconds)
				{
					case 60:
					case 50:
					case 40:
					case 30:
					case 20:
					case 10:
					case 5:
					case 4:
					case 3:
					case 2:
						Bukkit.broadcastMessage(BWMain.prefix + "§eDas Spiel startet in §a" + seconds + " §eSekunden!");
						break;
					case 1:
						Bukkit.broadcastMessage(BWMain.prefix + "§eDas Spiel startet in §aeiner §eSekunde!");
						break;
					case 0:
						Bukkit.getScheduler().cancelTask(taskID);
						break;
				}
				seconds--;
			}
		}, 0, 20);
	}

	@Override
	public void stop()
	{
		if (isRunning)
		{
			isRunning = false;
			Bukkit.getScheduler().cancelTask(taskID);
			seconds = 60;
			GameState.setCurrentState(GameState.INGAME);
		}
	}

	public void startIdle()
	{
		idleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{

			public void run()
			{
				Bukkit.broadcastMessage(BWMain.prefix + "§eDas Spiel benötigt noch §6"
						+ (plugin.min_players - Bukkit.getOnlinePlayers().size()) + " §eSpieler zum starten!");
			}
		}, 0, 20L);
	}

}
