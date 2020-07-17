package me.didi.utils;

import org.bukkit.entity.Player;

import me.didi.BWMain;
import me.didi.utils.voting.Map;

public class Utils
{

	static BWMain plugin = BWMain.getInstance();

	public void addToTeam(Player p, GameTeam team)
	{
		if (!team.isMember(p) && team.getMaxPlayers() > team.getPlayers().size())
		{
			team.getPlayers().add(p.getDisplayName());
			p.sendMessage(BWMain.prefix + "§aDu hast das Team " + team.getPrefix() + team.getName() + " §abetreten!");

		} else
		{
			p.sendMessage(BWMain.prefix + "§cDu kannst dieses Team nicht betreten!");
		}
	}

	public static GameTeam getTeam(Map map, String name)
	{
		GameTeam team = new GameTeam(map,
				plugin.getConfig()
						.getString("Maps." + map.getName().toUpperCase() + ".teams." + name.toUpperCase() + ".name"),
				plugin.getConfig()
						.getString("Maps." + map.getName().toUpperCase() + ".teams." + name.toUpperCase() + ".prefix"),
				(byte) (plugin.getConfig()
						.getInt("Maps." + map.getName() + ".teams." + name.toUpperCase() + ".colordata")));
		return team;
	}
}
