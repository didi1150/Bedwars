package me.didi.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.didi.BWMain;
import me.didi.commands.SubCommand;
import me.didi.utils.GameTeam;
import me.didi.utils.GameManager;
import me.didi.utils.voting.Map;
import net.md_5.bungee.api.ChatColor;

public class ShopCmd extends SubCommand
{
	private BWMain plugin;
	private GameManager gameManager;

	public ShopCmd(BWMain plugin)
	{
		this.plugin = plugin;
		this.gameManager = plugin.getGameManager();
	}

	@Override
	public String getName()
	{
		return "setshop";
	}

	@Override
	public String getDescription()
	{
		return "Setzt den Shop des jeweiligen Teams";
	}

	@Override
	public String getSyntax()
	{
		return "/bw setshop <team> <map>";
	}

	@Override
	public void execute(Player player, String[] args)
	{
		if (args.length > 1)
		{
			GameTeam team = gameManager.getTeam(args[1]);
			Map map = new Map(plugin, args[2]);
			if (map.exists())
			{
				if (team.exists())
				{
					gameManager.setTeamShop(map, team, player);
					player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast den Shop von Team " + team.getPrefix()
							+ team.getName() + ChatColor.GREEN + " auf der Map " + ChatColor.YELLOW + map.getName()
							+ ChatColor.GREEN + " erfolgreich gesetzt!");

				} else
					player.sendMessage(BWMain.prefix + ChatColor.RED + "Dieses Team existiert nicht!");
			} else
				player.sendMessage(BWMain.prefix + ChatColor.RED + "Diese Map existiert nicht!");
		}
	}

	@Override
	public List<String> getSubCommandArgs(Player player, String args[])
	{
		final List<String> completions = new ArrayList<String>();

		switch (args.length)
		{
			case 2:
				List<String> teams = new ArrayList<String>();
				for (GameTeam team : gameManager.getTeams())
				{
					teams.add(team.getName());
				}
				StringUtil.copyPartialMatches(args[1], teams, completions);
				Collections.sort(completions);
				return completions;
			case 3:
				List<String> maps = new ArrayList<String>();
				for (Map map : gameManager.getMaps())
				{
					maps.add(map.getName());
				}
				StringUtil.copyPartialMatches(args[2], maps, completions);
				Collections.sort(completions);
				return completions;
		}
		return null;
	}

}
