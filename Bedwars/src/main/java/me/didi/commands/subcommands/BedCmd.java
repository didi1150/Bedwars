package me.didi.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.didi.BWMain;
import me.didi.commands.SubCommand;
import me.didi.utils.GameManager;
import me.didi.utils.GameTeam;
import me.didi.utils.voting.Map;
import net.md_5.bungee.api.ChatColor;

public class BedCmd extends SubCommand
{

	BWMain plugin;
	GameManager gameManager;

	public BedCmd(BWMain plugin)
	{
		this.plugin = plugin;
		this.gameManager = plugin.getGameManager();
	}

	@Override
	public String getName()
	{
		return "setbed";
	}

	@Override
	public String getDescription()
	{
		return "sets the bed of the team";
	}

	@Override
	public String getSyntax()
	{
		return "/bw setbed <teamname> <map>";
	}

	@Override
	public void execute(Player player, String[] args)
	{
		String face = gameManager.getClosestFace(player);
		Map map = new Map(plugin, args[2]);
		GameTeam team = gameManager.getTeam(args[1]);
		if (map.exists())
		{
			if (team.exists())
			{
				gameManager.setBlockLocation(map, player.getLocation(), "teams." + team.getName() + ".bed");
				plugin.getConfig().set("Maps." + map.getName() + ".teams." + team.getName() + ".bed.face", face);
				plugin.saveConfig();
				player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast das Bett von Team " + team.getPrefix()
						+ team.getName() + ChatColor.GREEN + " gesetzt!");
			} else
				player.sendMessage(BWMain.prefix + ChatColor.RED + "Dieses Team existiert nicht!");
		} else
			player.sendMessage(BWMain.prefix + ChatColor.RED + "Diese Map existiert nicht!");
	}

	@Override
	public List<String> getSubCommandArgs(Player player, String[] args)
	{
		final List<String> completions = new ArrayList<String>();
		final List<String> list = new ArrayList<String>();
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
