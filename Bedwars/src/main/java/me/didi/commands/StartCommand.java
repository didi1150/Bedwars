package me.didi.commands;

import java.util.List;

import org.bukkit.entity.Player;

import me.didi.BWMain;
import me.didi.utils.gamestates.LobbyState;

public class StartCommand extends SubCommand
{

	BWMain plugin;

	public StartCommand(BWMain plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public String getName()
	{
		return "start";
	}

	@Override
	public String getDescription()
	{
		return "starts the game";
	}

	@Override
	public String getSyntax()
	{
		return "/bw start";
	}

	@Override
	public void execute(Player player, String[] args)
	{
		if (plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)
		{
			LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
			if (lobbyState.getCountDown().isRunning() && lobbyState.getCountDown().getSeconds() > 15)
			{
				lobbyState.getCountDown().setSeconds(15);
				player.sendMessage(BWMain.prefix + "§aDer CountDown wurde verkürzt.");
			}
		} else
			player.sendMessage(BWMain.prefix + "§cDas Spiel hat schon gestartet!");
	}

	@Override
	public List<String> getSubCommandArgs(Player player, String[] args)
	{
		return null;
	}

}
