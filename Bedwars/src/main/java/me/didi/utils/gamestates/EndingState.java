package me.didi.utils.gamestates;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.didi.BWMain;
import me.didi.utils.countdowns.EndingCountDown;

public class EndingState extends GameState
{

	private EndingCountDown endingCountDown;

	public EndingState(BWMain plugin)
	{
		this.endingCountDown = new EndingCountDown(plugin);
	}

	@Override
	public void start()
	{
		endingCountDown.start();
	}

	@Override
	public void stop()
	{
		Bukkit.getOnlinePlayers()
				.forEach(all -> all.kickPlayer(BWMain.prefix + ChatColor.DARK_PURPLE + "Das Spiel startet neu!"));
		Bukkit.shutdown();
	}

}
