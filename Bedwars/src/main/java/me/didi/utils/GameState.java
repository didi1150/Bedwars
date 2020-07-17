package me.didi.utils;

public enum GameState
{

	LOBBY, INGAME, ENDING;
	private static GameState currentState;

	public static void setCurrentState(GameState state)
	{
		currentState = state;
	}

	public static GameState getCurrentState()
	{
		return currentState;
	}

	public static boolean isState(GameState state)
	{
		return state == currentState;
	}

}
