package me.didi.utils.gamestates;

import me.didi.BWMain;
import me.didi.utils.countdowns.LobbyCountDown;

public class LobbyState extends GameState
{

	private LobbyCountDown countDown;
	BWMain plugin = BWMain.getInstance();

	public LobbyState(GameStateManager gameStateManager)
	{
		countDown = new LobbyCountDown(gameStateManager);
	}

	@Override
	public void start()
	{
		countDown.startIdle();
	}

	@Override
	public void stop()
	{

	}

	public LobbyCountDown getCountDown()
	{
		return countDown;
	}

}
