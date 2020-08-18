package me.didi.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import me.didi.utils.GameTeam;
import me.didi.utils.ItemBuilder;
import me.didi.utils.GameManager;
import me.didi.utils.countdowns.LobbyCountDown;
import me.didi.utils.gamestates.LobbyState;
import me.didi.utils.voting.Voting;

public class PlayerListener implements Listener
{

	private ItemStack vote;
	private ItemStack team;
	private ItemStack back;
	private BWMain plugin;
	private Voting voting;
	private GameManager gameManager;

	public PlayerListener(BWMain plugin)
	{
		this.plugin = plugin;
		this.vote = new ItemBuilder(Material.PAPER).setDisplayName(Voting.VOTING_INVENTORY_STRING).build();
		this.team = new ItemBuilder(Material.BED).setDisplayName(GameTeam.TEAM_INVENTORY_NAME).build();
		this.back = new ItemBuilder(Material.SLIME_BALL).setDisplayName("§cLobby").build();
		this.gameManager = plugin.getGameManager();
		this.voting = plugin.getVoting();
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
				p.getInventory().setItem(0, team);
				p.getInventory().setItem(8, back);
				p.setLevel(0);
				p.setHealth(20);
				p.setFoodLevel(20);
				plugin.getPlayers().add(p);
				p.setGameMode(GameMode.ADVENTURE);
				gameManager.addToRandomTeam(p);
				for (GameTeam team : gameManager.getTeams())
				{
					if (team.hasMember(p))
					{
						e.setJoinMessage(BWMain.prefix + team.getPrefix().replaceAll("&", "§") + p.getName()
								+ " §ahat das Spiel betreten! §r[§6" + Bukkit.getOnlinePlayers().size() + "§7/§6"
								+ plugin.getMin_players() + "§r]");
						break;
					}
				}
				LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
				LobbyCountDown lobbyCountDown = lobbyState.getCountDown();
				if (plugin.getPlayers().size() >= plugin.getMin_players())
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

				} else if (e.getItem().getType().equals(Material.BED))
				{
					plugin.openTeamInventory(p);
				} else if (e.getItem().getType().equals(Material.SLIME_BALL))
				{
					p.kickPlayer(null);
				}

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
			if (e.getView().getTitle().equalsIgnoreCase(Voting.VOTING_INVENTORY_STRING))
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

			if (e.getView().getTitle().equalsIgnoreCase(GameTeam.TEAM_INVENTORY_NAME))
			{
				for (GameTeam team : gameManager.getTeams())
				{
					e.setCancelled(true);
					if (e.getCurrentItem().getItemMeta().getDisplayName()
							.equalsIgnoreCase(team.getPrefix().replaceAll("&", "§") + team.getName()))
					{
						if (team.getMembers().size() < plugin.getMaxplayerperteam())
						{
							team.addMember(p);
							p.closeInventory();
							p.sendMessage(BWMain.prefix + "§aDu bist dem Team " + team.getPrefix().replaceAll("&", "§")
									+ team.getName() + " §abeigetreten!");
						} else
						{
							p.closeInventory();
							p.sendMessage(BWMain.prefix + "§cDas Team ist voll!");
						}
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
						+ plugin.getPlayers().size() + "§7/§6" + plugin.getMin_players() + "§r]");
				LobbyState lobbyState = (LobbyState) plugin.getGameStateManager().getCurrentGameState();
				LobbyCountDown lobbyCountDown = lobbyState.getCountDown();
				if (plugin.getPlayers().size() < plugin.getMin_players())
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
