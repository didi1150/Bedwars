package me.didi.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.didi.BWMain;
import me.didi.utils.GameTeam;
import me.didi.utils.Utils;
import me.didi.utils.voting.Map;

public class ShopCommand implements CommandExecutor
{

	BWMain plugin;

	public ShopCommand(BWMain plugin)
	{
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player p = (Player) sender;
			if (p.hasPermission("bw.setup"))
			{
				if (args.length == 2)
				{
					Map map = new Map(plugin, args[1]);
					if (map.exists())
					{
						GameTeam team = Utils.getTeam(args[2]);
						if (team.exists())
						{
							Utils.setTeamShop(map, team, p);
						} else
							p.sendMessage(BWMain.prefix + "§cDas Team existiert nicht!");
					} else
						p.sendMessage(BWMain.prefix + "§cDie Map existiert nicht!");
				} else
					p.sendMessage(BWMain.prefix + "§6/shop <map> <team> §c!");
				spawnShop(p.getLocation());
				p.sendMessage(BWMain.prefix + "§aDu hast den Shop erfolgreich gesetzt!");
			} else
				p.sendMessage(BWMain.prefix + "§cDu hast nicht die benötigten Rechte dafür!");
		} else
			sender.sendMessage(BWMain.prefix + "§cNur Spieler können diesen Command verwenden!");
		return false;
	}

	public void spawnShop(Location location)
	{
		String name = "§6Shop";

		Villager shop = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		shop.setCustomName(name);
		shop.setCustomNameVisible(true);
		shop.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 500, true));
		shop.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 500, true));
		shop.setProfession(Profession.LIBRARIAN);

	}

}
