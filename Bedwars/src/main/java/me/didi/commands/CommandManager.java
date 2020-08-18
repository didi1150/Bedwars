package me.didi.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.didi.BWMain;
import me.didi.commands.subcommands.CreateCmd;
import me.didi.commands.subcommands.DeleteCmd;
import me.didi.commands.subcommands.ShopCmd;

public class CommandManager implements TabExecutor
{

	private BWMain plugin;
	private ArrayList<SubCommand> subCommands = new ArrayList<SubCommand>();

	public CommandManager(BWMain plugin)
	{
		this.plugin = plugin;
		subCommands.add(new ShopCmd(plugin));
		subCommands.add(new CreateCmd(plugin));
		subCommands.add(new DeleteCmd(plugin));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;

			if (player.hasPermission("bw.cmd"))
			{
				if (args.length > 0)
				{
					for (int i = 0; i < getSubCommands().size(); i++)
					{
						if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName()))
						{
							getSubCommands().get(i).execute(player, args);
						}
					}
				}
			} else
				player.sendMessage(BWMain.prefix + ChatColor.RED + "Du hast nicht die Berechtigungen dafür!");
		}
		return false;
	}

	public ArrayList<SubCommand> getSubCommands()
	{
		return subCommands;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length == 1)
		{
			List<String> commands = new ArrayList<String>();
			for (SubCommand subCommand : getSubCommands())
			{
				commands.add(subCommand.getName());
			}
			final List<String> completions = new ArrayList<String>();
			StringUtil.copyPartialMatches(args[0], commands, completions);
			Collections.sort(completions);
			return completions;
		} else if (args.length >= 2)
		{
			for (int i = 0; i < getSubCommands().size(); i++)
			{
				if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName()))
				{
					return getSubCommands().get(i).getSubCommandArgs((Player) sender, args);
				}
			}
		}
		return null;
	}

	public BWMain getPlugin()
	{
		return plugin;
	}

}
