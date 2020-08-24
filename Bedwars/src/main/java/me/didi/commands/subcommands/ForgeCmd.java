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

public class ForgeCmd extends SubCommand
{

	GameManager gameManager;
	BWMain plugin;

	public ForgeCmd(BWMain plugin)
	{
		this.plugin = plugin;
		this.gameManager = plugin.getGameManager();
	}

	@Override
	public String getName()
	{
		return "setforge";
	}

	@Override
	public String getDescription()
	{
		return "sets the teams forge in the given map";
	}

	@Override
	public String getSyntax()
	{
		return "/bw setforge <team> <map>";
	}

	@Override
	public void execute(Player player, String[] args)
	{
		if (args.length == 3)
		{
			try
			{
				GameTeam team = gameManager.getTeam(args[1]);
				Map map = new Map(plugin, args[2]);
				gameManager.setBlockLocation(map, player.getLocation(), "teams." + args[1].toUpperCase() + ".spawner");
				player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast die Forge von Team " + team.getPrefix()
						+ team.getName() + " erfolgreich gesetzt!");
			} catch (Exception e)
			{
				player.sendMessage(BWMain.prefix + ChatColor.RED
						+ "Ein Fehler ist aufgetreten! Bitte kontaktiere den Entwickler!");
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<String> getSubCommandArgs(Player player, String[] args)
	{
		List<String> completions = new ArrayList<String>();
		List<String> list = new ArrayList<String>();
		if (args.length == 2)
		{
			for (GameTeam team : gameManager.getTeams())
			{
				list.add(team.getName());
			}

			StringUtil.copyPartialMatches(args[1], list, completions);
			Collections.sort(completions);
			return completions;
		} else if (args.length == 3)
		{
			for (Map map : gameManager.getMaps())
			{
				list.add(map.getName());
			}

			StringUtil.copyPartialMatches(args[2], list, completions);
			Collections.sort(completions);
			return completions;
		}
		return null;
	}

}
