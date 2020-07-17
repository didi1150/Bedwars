package me.didi.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.didi.utils.GameState;

public class BlockListener implements Listener
{

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if(GameState.isState(GameState.LOBBY)) {
			e.setCancelled(false);
		}
	}
	
}
