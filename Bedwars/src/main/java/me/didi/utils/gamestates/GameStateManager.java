package me.didi.utils.gamestates;

import me.didi.BWMain;

public class GameStateManager
{

	private BWMain plugin;
	private GameState[] gameStates;
	private GameState currentGameState;

	public GameStateManager(BWMain plugin)
	{
		this.plugin = plugin;
		gameStates = new GameState[3];

		gameStates[GameState.LOBBY_STATE] = new LobbyState(this);
		gameStates[GameState.INGAME_STATE] = new IngameState();
		gameStates[GameState.ENDING_STATE] = new EndingState(plugin);
	}

	public void setGameState(int gameStateID)
	{
		if (currentGameState != null)
			currentGameState.stop();
		currentGameState = gameStates[gameStateID];
		currentGameState.start();
	}

	public void stopCurrentGameState()
	{
		if (currentGameState != null)
		{
			currentGameState.stop();
			currentGameState = null;
		}
	}

	public GameState getCurrentGameState()
	{
		return currentGameState;
	}

	public BWMain getPlugin()
	{
		return plugin;
	}

}
