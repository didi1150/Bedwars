package me.didi.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.didi.BWMain;
import me.didi.commands.SubCommand;
import me.didi.utils.GameManager;
import me.didi.utils.GameTeam;
import me.didi.utils.voting.Map;

public class CreateCmd extends SubCommand
{

	BWMain plugin;
	GameManager gameManager;

	public CreateCmd(BWMain plugin)
	{
		this.plugin = plugin;
		this.gameManager = plugin.getGameManager();
	}

	@Override
	public String getName()
	{
		return "create";
	}

	@Override
	public String getDescription()
	{
		return "Erstellt Teams/Maps";
	}

	@Override
	public String getSyntax()
	{
		return "/bw create team <name> <color> | /bw create map <name> <builder>";
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(Player player, String[] args)
	{
		if (args.length == 4)
		{
			if (args[1].equalsIgnoreCase("team"))
			{
				ChatColor prefix = ChatColor.valueOf(args[4]);
				DyeColor color = DyeColor.valueOf(args[3]);
				GameTeam team = new GameTeam(args[2], prefix.toString(), color.getData());

				if (!team.exists())
				{
					team.create();
					player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast erfolgreich das Team "
							+ team.getPrefix() + team.getName() + ChatColor.GREEN + " erstellt!");
				}
			} else if (args[1].equalsIgnoreCase("map"))
			{
				Map map = new Map(plugin, args[2]);
				if (!map.exists())
				{
					map.create(args[3]);
					player.sendMessage(BWMain.prefix + ChatColor.GREEN + "Du hast erfolgreich die Map "
							+ ChatColor.YELLOW + map.getName() + ChatColor.GREEN + " erstellt!");
				}
			}
		}
	}

	@Override
	public List<String> getSubCommandArgs(Player player, String[] args)
	{
		final List<String> completions = new ArrayList<String>();
		final List<String> list = new ArrayList<String>();
		switch (args.length)
		{
			case 2:
				list.add("team");
				list.add("map");
				StringUtil.copyPartialMatches(args[1], list, completions);
				Collections.sort(completions);
				return completions;
			case 4:
				if (args[1].equalsIgnoreCase("team"))
				{
					for (int i = 0; i < gameManager.getColors().length; i++)
					{
						list.add(gameManager.getColors()[i]);
					}
					StringUtil.copyPartialMatches(args[3], list, completions);
					Collections.sort(completions);
					return completions;
				}
		}

		return null;
	}

}
