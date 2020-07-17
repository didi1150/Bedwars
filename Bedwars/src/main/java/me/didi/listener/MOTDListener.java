package me.didi.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import me.didi.BWMain;
import me.didi.utils.GameState;

public class MOTDListener implements Listener
{
	@EventHandler
	public void onPing(ServerListPingEvent e)
	{
		if (BWMain.getInstance().isFinished())
		{
			e.setMotd("INGAME");
		} else
		{
			if (GameState.isState(GameState.LOBBY))
				e.setMotd("§aLOBBY");
			else
				e.setMotd("INGAME");
		}
	}
}
