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

public class DeleteCmd extends SubCommand
{

	BWMain plugin;
	GameManager gameManager;

	public DeleteCmd(BWMain plugin)
	{
		this.plugin = plugin;
		this.gameManager = plugin.getGameManager();
	}

	@Override
	public String getName()
	{
		return "delete";
	}

	@Override
	public String getDescription()
	{
		return "Löscht Teams/Maps";
	}

	@Override
	public String getSyntax()
	{
		return "/bw delete [team/map] <name>";
	}

	@Override
	public void execute(Player player, String[] args)
	{
		if (args.length == 3)
		{
			if (args[1].equalsIgnoreCase("team"))
			{
				GameTeam team = gameManager.getTeam(args[2]);
				if (team.exists())
				{
					team.delete();
					player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast erfolgreich das Team "
							+ team.getPrefix() + team.getName() + ChatColor.GREEN + " gelöscht!");
				} else
					player.sendMessage(BWMain.prefix + ChatColor.RED + "Das Team existiert nicht!");
			} else if (args[1].equalsIgnoreCase("map"))
			{
				Map map = new Map(plugin, args[2]);
				if (map.exists())
				{
					map.delete();
					player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast erfolgreich die Map " + map.getName()
							+ " gelöscht!");
				} else
					player.sendMessage(BWMain.prefix + ChatColor.RED + "Die Map existiert nicht!");
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
			list.add("map");
			StringUtil.copyPartialMatches(args[1], list, completions);
			Collections.sort(completions);
			return completions;
		} else if (args.length == 3)
		{
			if (args[0].equalsIgnoreCase("team"))
			{
				for (GameTeam team : gameManager.getTeams())
				{
					list.add(team.getName());
				}
				StringUtil.copyPartialMatches(args[2], list, completions);
				Collections.sort(completions);
				return completions;
			} else if (args[0].equalsIgnoreCase("map"))
			{
				for (Map map : gameManager.getMaps())
				{
					list.add(map.getName());
				}

				StringUtil.copyPartialMatches(args[2], list, completions);
				Collections.sort(completions);
				return completions;
			}
		}
		return null;
	}

}
