package me.didi.commands;

import org.bukkit.Material;
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
import me.didi.utils.Utils;
import me.didi.utils.voting.Map;

public class BWCommand implements CommandExecutor
{

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
						if (args.length == 5)
						{
							try
							{
								Map map = new Map(BWMain.getInstance(), args[1]);
								if (map.exists())
								{
									GameTeam team = new GameTeam(new Map(BWMain.getInstance(), args[1].toUpperCase()),
											args[2].toUpperCase(), args[3], Byte.parseByte(args[4]));
									if (!team.exists(map))
									{
										team.addTeam(new Map(BWMain.getInstance(), args[1].toUpperCase()));
										p.sendMessage(BWMain.prefix + "§aDu hast erfolgreich das Team "
												+ args[3].toUpperCase().replaceAll("&", "§") + args[2].toUpperCase()
												+ " §aerfolgreich zur Map §6" + map.getName() + " §ageaddet!");
									} else
										p.sendMessage(BWMain.prefix + "§cDas Team existiert schon!");
								} else
								{
									p.sendMessage(BWMain.prefix + "§cDie Map §6" + args[0] + " §cexistiert nicht!");
								}
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
								GameTeam team = Utils.getTeam(map, args[2]);
								if (team.exists(map))
								{
									team.delete(map);
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
						} else
							p.sendMessage(BWMain.prefix + "§cBitte verwende §6/finish!");
					} else if (args[0].equalsIgnoreCase("edit"))
					{
						if (args.length == 1)
						{
							BWMain.getInstance().getConfig().set("finished", false);
							BWMain.getInstance().saveConfig();
						} else
							p.sendMessage(BWMain.prefix + "§cBitte verwende §6/edit!");
					} else if (args[0].equalsIgnoreCase("createSpawner"))
					{
						if (args.length == 5)
						{
							Map map = new Map(BWMain.getInstance(), args[1]);
							if (args[3].equalsIgnoreCase("IRON"))
							{
								Spawner spawner = new Spawner(map, p.getLocation(), Integer.parseInt(args[2]),
										SpawnCategory.IRON, Integer.parseInt(args[4]));
								if (spawner.exists())
								{
									spawner.addSpawner(map);
								} else
									p.sendMessage(BWMain.getInstance() + "§cDer Spawner mit der ID §6" + args[2]
											+ " §cexistiert schon!");
							}
							if (args[3].equalsIgnoreCase("GOLD"))
							{
								Spawner spawner = new Spawner(map, p.getLocation(), Integer.parseInt(args[2]),
										SpawnCategory.GOLD, Integer.parseInt(args[4]));
								if (spawner.exists())
								{
									spawner.addSpawner(map);
								} else
									p.sendMessage(BWMain.getInstance() + "§cDer Spawner mit der ID §6" + args[2]
											+ " §cexistiert schon!");
							}
							if (args[3].equalsIgnoreCase("EMERALD"))
							{
								Spawner spawner = new Spawner(map, p.getLocation(), Integer.parseInt(args[2]),
										SpawnCategory.EMERALD, Integer.parseInt(args[4]));
								if (spawner.exists())
								{
									spawner.addSpawner(map);
								} else
									p.sendMessage(BWMain.getInstance() + "§cDer Spawner mit der ID §6" + args[2]
											+ " §cexistiert schon!");
							}
						} else
							p.sendMessage(BWMain.getInstance()
									+ "§cBitte verwende §6/bw createSpawner <map> <delay> <IRON || GOLD || EMERALD> <ID>§c!");
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
								GameTeam t = Utils.getTeam(map, args[2]);
								if (t.exists(map))
								{
									t.setSpawn(map, p.getLocation());
									p.sendMessage(BWMain.prefix + "§aDu hast erfolgreich den Spawn von Team "
											+ t.getPrefix() + t.getName() + " §agesetzt!");
								} else
									p.sendMessage(BWMain.prefix + "§cDas Team existiert nicht!");
							}else p.sendMessage(BWMain.prefix + "§cDie Map existiert nicht!");
						} else
							p.sendMessage(BWMain.prefix + "§cVerwende §6/bw setspawn <map> <team>!");
					}
				}
			}
		}
		return false;
	}

}
