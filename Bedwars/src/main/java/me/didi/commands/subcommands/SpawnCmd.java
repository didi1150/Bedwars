package me.didi.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.didi.BWMain;
import me.didi.commands.SubCommand;
import me.didi.utils.GameManager;
import me.didi.utils.GameTeam;
import me.didi.utils.voting.Map;

public class SpawnCmd extends SubCommand
{

	BWMain plugin;
	GameManager gameManager;

	public SpawnCmd(BWMain plugin)
	{
		this.plugin = plugin;
		this.gameManager = plugin.getGameManager();
	}

	@Override
	public String getName()
	{
		return "setspawn";
	}

	@Override
	public String getDescription()
	{
		return "Sets the spawn";
	}

	@Override
	public String getSyntax()
	{
		return "/bw setspawn team <name> <map> | /bw setspawn spectator <map> | /bw setspawn lobby";
	}

	@Override
	public void execute(Player player, String[] args)
	{
		if (args.length > 1)
		{

			if (args.length == 4)
			{
				Map map = new Map(plugin, args[3]);
				GameTeam team = gameManager.getTeam(args[2]);
				if (team.exists())
				{
					team.saveSpawn(map, player.getLocation());
					player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast den Spawn von Team "
							+ team.getPrefix() + team.getName() + ChatColor.GREEN + " gesetzt!");
				}
			} else if (args.length == 3)
			{
				Map map = new Map(plugin, args[2]);
				if (map.exists())
				{
					map.setSpectatorSpawnLocation(player.getLocation());
					player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast den Spectator-Spawn von Team "
							+ ChatColor.YELLOW + map.getName() + ChatColor.GREEN + " gesetzt!");
				}
			} else if (args.length == 2)
			{
				if (args[1].equalsIgnoreCase("lobby"))
				{
					gameManager.setLobby(player.getLocation());
					player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast den Lobby-Spawn gesetzt!");
				}
			}

		}
	}

	@Override
	public List<String> getSubCommandArgs(Player player, String[] args)
	{
		final List<String> completions = new ArrayList<String>();
		final List<String> list = new ArrayList<String>();
		if (args.length == 2)
		{
			list.add("team");
			list.add("spectator");
			list.add("lobby");
			StringUtil.copyPartialMatches(args[1], list, completions);
			Collections.sort(completions);
			return completions;
		} else if (args.length == 3)
		{
			if (args[1].equalsIgnoreCase("team"))
			{
				for (GameTeam team : gameManager.getTeams())
				{
					list.add(team.getName());
				}
				StringUtil.copyPartialMatches(args[2], list, completions);
				Collections.sort(completions);
				return completions;
			} else if (args[1].equalsIgnoreCase("spectator"))
			{
				for (Map map : gameManager.getMaps())
				{
					list.add(map.getName());
				}
				StringUtil.copyPartialMatches(args[2], list, completions);
				Collections.sort(completions);
				return completions;
			}

		} else if (args.length == 4)
		{
			if (args[1].equalsIgnoreCase("team"))
			{
				for (Map map : gameManager.getMaps())
				{
					list.add(map.getName());
				}
				StringUtil.copyPartialMatches(args[3], list, completions);
				Collections.sort(completions);
				return completions;
			}
		}
		return null;
	}

}
