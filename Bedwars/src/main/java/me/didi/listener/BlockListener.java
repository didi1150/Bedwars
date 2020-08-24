package me.didi.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.didi.BWMain;
import me.didi.utils.GameManager;
import me.didi.utils.GameTeam;
import me.didi.utils.gamestates.EndingState;
import me.didi.utils.gamestates.GameStateManager;
import me.didi.utils.gamestates.IngameState;
import me.didi.utils.gamestates.LobbyState;
import net.md_5.bungee.api.ChatColor;

public class BlockListener implements Listener
{

	BWMain plugin;
	GameStateManager gameStateManager;
	GameManager gameManager;

	public BlockListener(BWMain plugin)
	{
		this.plugin = plugin;
		this.gameStateManager = plugin.getGameStateManager();
		this.gameManager = plugin.getGameManager();
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		if (plugin.isFinished())
		{
			if (gameStateManager.getCurrentGameState() instanceof LobbyState
					|| gameStateManager.getCurrentGameState() instanceof EndingState)
			{
				e.setCancelled(true);
				return;
			} else
			{
				if (p.getGameMode().equals(GameMode.CREATIVE))
					return;
				if (e.getBlock().getType() == Material.TNT)
				{
					ItemStack item = p.getItemInHand();

					if (item.getAmount() > 1)
						item.setAmount(item.getAmount() - 1);
					else
						item = new ItemStack(Material.AIR);

					p.getInventory().setItemInHand(item);

					e.setCancelled(true);
					e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);
					return;
				}

				if (!gameManager.getBuildedBlocks().contains(e.getBlock().getLocation()))
				{
					gameManager.getBuildedBlocks().add(e.getBlock().getLocation());
					return;
				}
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e)
	{
		Location loc = e.getBlock().getLocation();
		if (plugin.isFinished())
		{
			if (gameStateManager.getCurrentGameState() instanceof IngameState)
			{
				if (e.getBlock().getType().equals(Material.BED_BLOCK) && plugin.getPlayers().contains(e.getPlayer()))
				{
					Player p = e.getPlayer();
					GameTeam team = null;
					GameTeam t = null;

					for (GameTeam gameTeam : gameManager.getTeams())
					{
						if (loc.equals(gameTeam.getBedBottom()) || loc.equals(gameTeam.getBedTop()))
						{
							team = gameTeam;
						}
					}

					for (GameTeam gameTeam : gameManager.getTeams())
					{
						if (gameTeam.hasMember(p))
						{
							t = gameTeam;
						}
					}

					if (!team.hasMember(p))
					{
						Block top = team.getBedTop().getBlock();
						Block bottom = team.getBedBottom().getBlock();

						top.getDrops().clear();
						top.setType(Material.AIR, false);

						bottom.getDrops().clear();
						bottom.setType(Material.AIR, false);

						Bukkit.getOnlinePlayers()
								.forEach(all -> all.playSound(all.getLocation(), Sound.ENDERDRAGON_GROWL, 5, 5));
						Bukkit.broadcastMessage(BWMain.prefix + t.getPrefix() + p.getDisplayName()
								+ ChatColor.RED + " hat das Bett von Team " + team.getPrefix() + team.getName()
								+ ChatColor.RED + " zerstört!");

					} else
					{
						e.setCancelled(true);

						p.sendMessage(BWMain.prefix + "Du kannst dein eigenes Bett §cnicht §7zerstören!");
					}
				} else if (!gameManager.getBuildedBlocks().contains(e.getBlock().getLocation()))
				{
					Player p = e.getPlayer();
					p.sendMessage(BWMain.prefix + ChatColor.RED + "Das kannst du nicht!");
					e.setCancelled(true);
				}
			}
		}
	}

}
