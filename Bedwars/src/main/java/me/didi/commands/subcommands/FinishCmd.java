package me.didi.commands.subcommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.didi.BWMain;
import me.didi.commands.SubCommand;

public class FinishCmd extends SubCommand
{

	BWMain plugin;

	public FinishCmd(BWMain plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public String getName()
	{
		return "finished";
	}

	@Override
	public String getDescription()
	{
		return "toggles edit/play mode";
	}

	@Override
	public String getSyntax()
	{
		return "/bw finished <false/true>";
	}

	@Override
	public void execute(Player player, String[] args)
	{
		if (args.length > 1)
		{
			if (args[1].equalsIgnoreCase("false"))
			{
				plugin.getConfig().set("finished", false);
				plugin.saveConfig();
				player.kickPlayer(BWMain.prefix + ChatColor.GREEN + "Server lädt neu!");
				Bukkit.shutdown();
			} else if (args[1].equalsIgnoreCase("true"))
			{
				plugin.getConfig().set("finished", true);
				plugin.saveConfig();
				player.kickPlayer(BWMain.prefix + ChatColor.GREEN + "Server lädt neu!");
				Bukkit.shutdown();
			}
		}
	}

	@Override
	public List<String> getSubCommandArgs(Player player, String[] args)
	{
		final List<String> completions = new ArrayList<String>();
		final List<String> list = new ArrayList<String>();
		if (args.length > 1)
		{
			list.add("false");
			list.add("true");
			StringUtil.copyPartialMatches(args[1], list, completions);
			Collections.sort(completions);
			return completions;
		}
		return null;
	}

}
