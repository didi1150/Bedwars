package me.didi.commands;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class SubCommand
{
	
	public abstract String getName();
	
	public abstract String getDescription();
	
	public abstract String getSyntax();
	
	public abstract void execute(Player player, String args[]);
	
	public abstract List<String> getSubCommandArgs(Player player, String args[]);
}
