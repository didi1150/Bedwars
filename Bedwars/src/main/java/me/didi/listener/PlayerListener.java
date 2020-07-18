package me.didi.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.didi.BWMain;
import me.didi.utils.ItemBuilder;
import me.didi.utils.countdowns.LobbyCountDown;
import me.didi.utils.gamestates.LobbyState;
import me.didi.utils.voting.Voting;

public class PlayerListener implements Listener
{

	private ItemStack vote;
	private BWMain plugin;
	private Voting voting = BWMain.getInstance().getVoting();

	public PlayerListener(BWMain plugin)
	{
		this.plugin = plugin;
		this.vote = new ItemBuilder(Material.PAPER).setDisplayName(Voting.VOTING_INVENTORY_STRING).build();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if (plugin.isFinished())
		{
			if (plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)
			{
				Player p = e.getPlayer();
				p.getInventory().clear();
				p.getInventory().setItem(1, vote);
				p.setLevel(0);
				plugin.getPlayers().add(p);
				e.setJoinMessage(BWMain.prefix + p.getName() + " §ahat das Spiel betreten! §r[§6"
						+ Bukkit.getOnlinePlayers().size() + "§7/§6" + plugin.min_players + "§r]");
				LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
				LobbyCountDown lobbyCountDown = lobbyState.getCountDown();
				if (plugin.getPlayers().size() >= plugin.min_players)
				{
					if (!lobbyCountDown.isRunning())
					{
						lobbyCountDown.stopIdle();
						lobbyCountDown.start();
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if (plugin.isFinished())
		{
			Player p = e.getPlayer();

			if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				if (e.getItem().getType().equals(Material.PAPER))
				{
					p.openInventory(voting.getVotingInventory());

				} else
					return;
			} else
				return;

		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		Player p = (Player) e.getWhoClicked();
		if (plugin.isFinished())
		{

			e.setCancelled(true);
			if (e.getClickedInventory().getTitle().equalsIgnoreCase(Voting.VOTING_INVENTORY_STRING))
			{
				for (int i = 0; i < voting.getVotingInventoryOrder().length; i++)
				{
					if (voting.getVotingInventoryOrder()[i] == e.getSlot())
					{
						voting.vote(p, i);
						return;
					}

				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		if (plugin.isFinished())
		{
			if (plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)
			{
				Player p = e.getPlayer();
				plugin.getPlayers().remove(p);
				e.setQuitMessage(BWMain.prefix + p.getName() + " §chat das Spiel verlassen! §r[§6"
						+ plugin.getPlayers().size() + "§7/§6" + plugin.min_players + "§r]");
				LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
				LobbyCountDown lobbyCountDown = lobbyState.getCountDown();
				if (plugin.getPlayers().size() < plugin.min_players)
				{
					if (lobbyCountDown.isRunning())
					{
						lobbyCountDown.stop();
						lobbyCountDown.startIdle();
					}
				}

				Voting voting = plugin.getVoting();
				if (voting.getPlayerVotes().containsKey(e.getPlayer().getName()))
				{
					voting.getVotingMaps()[voting.getPlayerVotes().get(e.getPlayer().getName())].removeVote();
					voting.initVotingInventory();
					voting.getPlayerVotes().remove(e.getPlayer().getName());
				}
			}

		}
	}
}
