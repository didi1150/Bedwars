package me.didi.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.didi.BWMain;
import me.didi.utils.GameTeam;
import me.didi.utils.SpawnCategory;
import me.didi.utils.Spawner;
import me.didi.utils.GameManager;
import me.didi.utils.countdowns.LobbyCountDown;
import me.didi.utils.gamestates.LobbyState;
import me.didi.utils.voting.Map;

public class BWCommand implements CommandExecutor
{

	BWMain plugin = BWMain.getInstance();
	public static int START_SECONDS = 15;
	GameManager gameManager = plugin.getGameManager();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player p = (Player) sender;
			if (p.hasPermission("bw.setup"))
			{
				if (args.length >= 1)
				{

					if (args[0].equalsIgnoreCase("create"))
					{
						if (args.length == 3)
						{
							Map map = new Map(BWMain.getInstance(), args[1]);
							if (!map.exists())
							{
								map.create(args[2]);
							}
							p.sendMessage(
									BWMain.prefix + "§aDu hast die Map §e" + args[1] + " §aerfolgreich erstellt!");
						} else
						{
							p.sendMessage(BWMain.prefix + "§cBenutze §6/bw create <name> <Erbauer>");
						}

					} else if (args[0].equalsIgnoreCase("addteam"))
					{
						if (args.length == 4)
						{
							try
							{
								GameTeam team = new GameTeam(args[1].toUpperCase(), args[2], Byte.parseByte(args[3]));
								if (!team.exists())
								{
									Bukkit.broadcastMessage("adden!");
									team.create();
									p.sendMessage(BWMain.prefix + "§aDu hast erfolgreich das Team "
											+ args[2].toUpperCase().replaceAll("&", "§") + args[1].toUpperCase()
											+ " §aerfolgreich geaddet!");
								} else
									p.sendMessage(BWMain.prefix + "§cDas Team existiert schon!");

							} catch (NumberFormatException e)
							{
								p.sendMessage(BWMain.prefix + "§cBitte gib gültige Ziffern ein!");
							}
						} else
						{
							p.sendMessage(BWMain.prefix
									+ "§cBitte verwende /§6bw addteam <map> <teamname> <prefix> <colordata> !");
						}
					} else if (args[0].equalsIgnoreCase("deleteTeam"))
					{
						if (args.length == 3)
						{

							Map map = new Map(BWMain.getInstance(), args[1]);
							if (map.exists())
							{
								GameTeam team = gameManager.getTeam(args[2]);
								if (team.exists())
								{
									team.delete();
									p.sendMessage(BWMain.prefix + "§aDu hast das Team " + team.getPrefix()
											+ team.getName() + " §aerfolgreich gelöscht!");
								} else
									p.sendMessage(BWMain.prefix + "§cDas Team existiert nicht!");
							} else
							{
								p.sendMessage(BWMain.prefix + "§cDie Map §6" + args[0] + " §cexistiert nicht!");
							}

						} else
						{
							p.sendMessage(BWMain.prefix + "§cBitte verwende /§6bw deleteteam <map> <teamname>!");
						}
					} else if (args[0].equalsIgnoreCase("deleteMap"))
					{
						if (args.length == 2)
						{
							Map map = new Map(BWMain.getInstance(), args[1]);
							if (map.exists())
							{
								map.delete();
							} else
							{
								p.sendMessage(BWMain.prefix + "§cDie Map §6" + args[1] + " §cexistiert nicht!");
							}
						} else
							p.sendMessage(BWMain.prefix + "§cVerwende bitte §6/bw deleteMap <map>");

					} else if (args[0].equalsIgnoreCase("finish"))
					{
						if (args.length == 1)
						{
							BWMain.getInstance().getConfig().set("finished", true);
							BWMain.getInstance().saveConfig();
							p.kickPlayer(BWMain.prefix + "§eServer lädt neu!");
							Bukkit.shutdown();
						} else
							p.sendMessage(BWMain.prefix + "§cBitte verwende §6/finish!");
					} else if (args[0].equalsIgnoreCase("edit"))
					{
						if (args.length == 1)
						{
							BWMain.getInstance().getConfig().set("finished", false);
							BWMain.getInstance().saveConfig();
							p.kickPlayer(BWMain.prefix + "§eServer lädt neu!");
							Bukkit.shutdown();
						} else
							p.sendMessage(BWMain.prefix + "§cBitte verwende §6/edit!");
					} else if (args[0].equalsIgnoreCase("createSpawner"))
					{
						if (args.length == 4)
						{
							Map map = new Map(plugin, args[1]);
							if (args[3].equalsIgnoreCase("IRON"))
							{
								int count = 0;

								if (plugin.getConfig().contains("Locations." + map.getName() + ".IronCount"))
									count = plugin.getConfig().getInt("Locations." + map.getName() + ".IronCount");

								plugin.getConfig().set("Locations." + map.getName() + ".IronCount", (count + 1));
								plugin.saveConfig();
								Spawner spawner = new Spawner(p.getLocation(), Integer.parseInt(args[2]));
								gameManager.setBlockLocation(map, spawner.getLocation(), "IRONSPAWNER-" + (count + 1));
								p.sendMessage(BWMain.prefix + "§aDu hast einen Iron-Spawner erstellt!");
							}
							if (args[3].equalsIgnoreCase("GOLD"))
							{
								int count = 0;

								if (plugin.getConfig().contains("Locations." + map.getName() + ".GoldCount"))
									count = plugin.getConfig().getInt("Locations." + map.getName() + ".GoldCount");

								plugin.getConfig().set("Locations." + map.getName() + ".GoldCount", (count + 1));
								plugin.saveConfig();
								Spawner spawner = new Spawner(p.getLocation(), Integer.parseInt(args[2]));
								gameManager.setBlockLocation(map, spawner.getLocation(), "GOLDSPAWNER-" + (count + 1));
								p.sendMessage(BWMain.prefix + "§aDu hast einen Gold-Spawner erstellt!");
							}
							if (args[3].equalsIgnoreCase("EMERALD"))
							{
								int count = 0;

								if (plugin.getConfig().contains("Locations." + map.getName() + ".EmeraldCount"))
									count = plugin.getConfig().getInt("Locations." + map.getName() + ".EmeraldCount");

								plugin.getConfig().set("Locations." + map.getName() + ".EmeraldCount", (count + 1));
								plugin.saveConfig();
								Spawner spawner = new Spawner(p.getLocation(), Integer.parseInt(args[2]));
								gameManager.setBlockLocation(map, spawner.getLocation(), "EMERALDSPAWNER-" + (count + 1));
								p.sendMessage(BWMain.prefix + "§aDu hast einen Emerald-Spawner erstellt!");
							}
						} else
							p.sendMessage(BWMain.getInstance()
									+ "§cBitte verwende §6/bw createSpawner <map> <delay> <IRON || GOLD || EMERALD> §c!");
					} else if (args[0].equalsIgnoreCase("setSpectatorSpawn"))
					{
						if (args.length == 2)
						{
							Map map = new Map(BWMain.getInstance(), args[1]);
							if (map.exists())
							{
								map.setSpectatorSpawnLocation(p.getLocation());
							}
						} else
							p.sendMessage(BWMain.prefix + "§cVerwende §6/bw setSpectatorSpawn <map> §c!");
					} else if (args[0].equalsIgnoreCase("setspawn"))
					{
						if (args.length == 3)
						{
							Map map = new Map(BWMain.getInstance(), args[1]);
							if (map.exists())
							{
								GameTeam t = gameManager.getTeam(args[2]);
								if (t.exists())
								{
									t.saveSpawn(map, p.getLocation());
									p.sendMessage(BWMain.prefix + "§aDu hast erfolgreich den Spawn von Team "
											+ t.getPrefix().replaceAll("&", "§") + t.getName() + " §agesetzt!");
								} else
									p.sendMessage(BWMain.prefix + "§cDas Team existiert nicht!");
							} else
								p.sendMessage(BWMain.prefix + "§cDie Map existiert nicht!");
						} else
							p.sendMessage(BWMain.prefix + "§cVerwende §6/bw setspawn <map> <team>!");
					} else if (args[0].equalsIgnoreCase("start"))
					{
						if (plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)
						{
							LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
							if (lobbyState.getCountDown().isRunning() && lobbyState.getCountDown().getSeconds() > 15)
							{
								lobbyState.getCountDown().setSeconds(START_SECONDS);
								p.sendMessage(BWMain.prefix + "§aDer CountDown wurde verkürzt.");
							}
						} else
							p.sendMessage(BWMain.prefix + "§cDas Spiel hat schon gestartet!");
					}
				}
			}
		}
		return false;
	}

}
