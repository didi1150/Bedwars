package me.didi.utils.countdowns;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.didi.BWMain;
import me.didi.utils.gamestates.GameState;
import me.didi.utils.gamestates.GameStateManager;
import me.didi.utils.voting.Map;
import me.didi.utils.voting.Voting;

public class LobbyCountDown extends Countdown
{

	private GameStateManager gameStateManager;
	private int idleID;
	private boolean isIdling;
	private boolean isRunning;
	private int seconds;

	public LobbyCountDown(GameStateManager gameStateManager)
	{
		this.gameStateManager = gameStateManager;
		seconds = 60;
	}

	@Override
	public void start()
	{
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable()
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
						Bukkit.broadcastMessage(BWMain.prefix + "§aDas Spiel startet in §6" + seconds + " §aSekunden!");
						break;
					case 10:
						Voting voting = BWMain.getInstance().getVoting();
						Map winningMap;
						if (voting != null)
						{
							winningMap = voting.getWinnerMap();
						} else
						{
							ArrayList<Map> maps = BWMain.getInstance().getMaps();
							Collections.shuffle(maps);
							winningMap = maps.get(0);
						}
						Bukkit.broadcastMessage(
								BWMain.prefix + "§aDie Map §6" + winningMap.getName() + " §awurde gewählt!");
						break;
					case 5:
					case 3:
					case 2:
						Bukkit.broadcastMessage(BWMain.prefix + "§aDas Spiel startet in §6" + seconds + " §aSekunden!");
						break;
					case 1:
						Bukkit.broadcastMessage(BWMain.prefix + "§aDas Spiel startet in §6einer §aSekunde!");
						break;
					case 0:
						gameStateManager.setGameState(GameState.INGAME_STATE);
						break;
				}
				seconds--;
			}
		}, 0, 20L);
	}

	@Override
	public void stop()
	{
		if (isRunning)
		{
			Bukkit.getScheduler().cancelTask(taskID);
			isRunning = false;
			seconds = 60;
		}
	}

	public void startIdle()
	{
		isIdling = true;
		idleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(gameStateManager.getPlugin(), new Runnable()
		{

			public void run()
			{
				Bukkit.broadcastMessage(BWMain.prefix + "§eDas Spiel braucht noch §6"
						+ (gameStateManager.getPlugin().min_players - Bukkit.getOnlinePlayers().size())
						+ " Spieler §ebis zum Spielstart!");
			}
		}, 0, 20 * 20);
	}

	public void stopIdle()
	{
		if (isIdling)
		{
			Bukkit.getScheduler().cancelTask(idleID);
			isIdling = false;
		}
	}

	public boolean isRunning()
	{
		return isRunning;
	}
}
