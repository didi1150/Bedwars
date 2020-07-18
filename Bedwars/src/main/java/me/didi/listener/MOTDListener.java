package me.didi.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import me.didi.BWMain;
import me.didi.utils.gamestates.GameState;
import me.didi.utils.gamestates.LobbyState;

public class MOTDListener implements Listener
{
	BWMain plugin = BWMain.getInstance();

	@EventHandler
	public void onPing(ServerListPingEvent e)
	{
		if (BWMain.getInstance().isFinished())
		{
			e.setMotd("INGAME");
		} else
		{
			if (plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)
				e.setMotd("§aLOBBY");
			else
				e.setMotd("INGAME");
		}
	}
}
