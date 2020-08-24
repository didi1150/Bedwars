package me.didi.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import me.didi.BWMain;
import me.didi.utils.GameManager;
import me.didi.utils.GameTeam;
import me.didi.utils.ItemBuilder;
import me.didi.utils.TitleAPI;
import me.didi.utils.countdowns.LobbyCountDown;
import me.didi.utils.gamestates.GameState;
import me.didi.utils.gamestates.GameStateManager;
import me.didi.utils.gamestates.IngameState;
import me.didi.utils.gamestates.LobbyState;
import me.didi.utils.shop.BedwarsShop;
import me.didi.utils.voting.Voting;

public class PlayerListener implements Listener
{

	private ItemStack vote;
	private ItemStack team;
	private ItemStack back;
	private BWMain plugin;
	private Voting voting;
	private GameManager gameManager;
	private GameStateManager gameStateManager;
	private HashMap<Player, Player> kills = new HashMap<Player, Player>();

	public PlayerListener(BWMain plugin, GameManager gameManager)
	{
		this.plugin = plugin;
		this.vote = new ItemBuilder(Material.PAPER).setDisplayName(Voting.VOTING_INVENTORY_STRING).build();
		this.team = new ItemBuilder(Material.BED).setDisplayName(GameTeam.TEAM_INVENTORY_NAME).build();
		this.back = new ItemBuilder(Material.SLIME_BALL).setDisplayName("§cLobby").build();
		this.gameManager = gameManager;
		this.voting = plugin.getVoting();
		this.gameStateManager = plugin.getGameStateManager();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if (plugin.isFinished())
		{
			if (plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState)
			{
				Player p = e.getPlayer();
				p.teleport(gameManager.getLobby());
				p.setGameMode(GameMode.ADVENTURE);
				p.getInventory().clear();
				p.getInventory().setItem(1, vote);
				p.getInventory().setItem(0, team);
				p.getInventory().setItem(8, back);
				p.setLevel(0);
				p.setHealth(20);
				p.setFoodLevel(20);
				plugin.getPlayers().add(p);
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
			} else if (gameStateManager.getCurrentGameState() instanceof IngameState)
			{
				for (GameTeam team : gameManager.getAliveTeams())
				{
					new BedwarsShop(team, plugin, "Shop",
							gameManager.getTeamShop(plugin.getVoting().getWinnerMap(), team)).spawn();
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
							.equalsIgnoreCase(team.getPrefix() + team.getName()))
					{
						if (team.getMembers().size() < plugin.getMaxplayerperteam())
						{
							team.addMember(p);
							p.closeInventory();
							p.sendMessage(BWMain.prefix + "§aDu bist dem Team " + team.getPrefix() + team.getName()
									+ " §abeigetreten!");
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
		Player p = e.getPlayer();
		if (plugin.isFinished())
		{
			if ((plugin.getGameStateManager().getCurrentGameState() instanceof LobbyState))
			{

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
			} else if (plugin.getGameStateManager().getCurrentGameState() instanceof IngameState)
			{
				if (plugin.getPlayers().contains(p))
				{
					plugin.getPlayers().remove(p);
					GameTeam team = gameManager.getTeamByPlayer(p);
					team.removeMember(p);
					e.setQuitMessage(BWMain.prefix + ChatColor.GRAY + "Der Spieler " + team.getPrefix()
							+ p.getDisplayName() + ChatColor.GRAY + " hat das Spiel verlassen");
					if (team.isEmpty())
					{
						if (team.bedExists())
						{
							team.getBedBottom().getBlock().setType(Material.AIR);
							team.getBedTop().getBlock().setType(Material.AIR);
						}
						Bukkit.broadcastMessage(BWMain.prefix + ChatColor.YELLOW + "Das Team " + team.getPrefix()
								+ team.getName() + ChatColor.YELLOW + " ist ausgeschieden!");
						winDetection();
					}
				}
			}

		}

	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player deathPlayer = e.getEntity();
		if (plugin.getPlayers().contains(deathPlayer))
		{
			GameTeam deathTeam = gameManager.getTeamByPlayer(deathPlayer);
			GameTeam attackTeam = null;

			if (kills.containsKey(deathPlayer))
			{
				Player killer = kills.get(deathPlayer);
				attackTeam = gameManager.getTeamByPlayer(killer);
				if (!deathTeam.bedExists())
				{
					plugin.getPlayers().remove(deathPlayer);
					deathTeam.removeMember(deathPlayer);
					if (deathTeam.isEmpty())
					{
						Bukkit.broadcastMessage(BWMain.prefix + "Das Team " + deathTeam.getPrefix()
								+ deathTeam.getName() + ChatColor.GRAY + " ist ausgeschieden!");

						winDetection();

					}
				} else
					e.setDeathMessage(BWMain.prefix + ChatColor.GRAY + "Der Spieler " + deathTeam.getPrefix()
							+ deathPlayer.getDisplayName() + ChatColor.GRAY + " wurde von " + attackTeam.getPrefix()
							+ killer.getDisplayName() + " erschlagen!");
				deathPlayer.spigot().respawn();
			} else
				e.setDeathMessage(BWMain.prefix + ChatColor.GRAY + "Der Spieler " + deathTeam.getPrefix()
						+ deathPlayer.getDisplayName() + ChatColor.GRAY + " ist gestorben!");
			deathPlayer.spigot().respawn();
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		if (plugin.getPlayers().contains(e.getPlayer()))
		{
			GameTeam team = gameManager.getTeamByPlayer(e.getPlayer());
			if (team.bedExists())
			{
				TitleAPI.sendTitle(e.getPlayer(), ChatColor.GREEN + "Respawnt!", "", 10, 30, 20);
				e.getPlayer().teleport(team.getSpawn());
			} else
				e.getPlayer().kickPlayer("Du bist ausgeschieden!");

		}
	}

	@EventHandler
	public void onDmg(EntityDamageEvent e)
	{
		if (!(gameStateManager.getCurrentGameState() instanceof IngameState))
		{
			e.setCancelled(true);
		} else
		{
			if (e.getCause() == DamageCause.VOID && gameStateManager.getCurrentGameState() instanceof IngameState)
			{
				Player p = (Player) e.getEntity();
				p.setHealth(0);
				p.spigot().respawn();
				p.playSound(p.getLocation(), Sound.BAT_DEATH, 1f, 1f);
			}
		}
	}

	@EventHandler
	public void onDmgByEntity(EntityDamageByEntityEvent e)
	{
		if (!(gameStateManager.getCurrentGameState() instanceof IngameState))
		{
			if (e.getEntity() instanceof Player && e.getDamager() instanceof Player)
			{
				final Player attacker = (Player) e.getDamager();
				final Player attackedPlayer = (Player) e.getEntity();
				kills.put(attackedPlayer, attacker);
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
				{

					public void run()
					{
						kills.remove(attackedPlayer);
					}
				}, 20 * 15);
			}
		} else
			return;
	}

	private void winDetection()
	{
		if (gameManager.getLastTeam() != null)
		{
			Bukkit.getOnlinePlayers()
					.forEach(all -> all.sendMessage(
							BWMain.prefix + ChatColor.YELLOW + "Das Team " + gameManager.getLastTeam().getPrefix()
									+ gameManager.getLastTeam().getName() + ChatColor.YELLOW + " hat gewonnen!"));
			gameStateManager.setGameState(GameState.ENDING_STATE);
			Bukkit.getOnlinePlayers().forEach(all -> all.teleport(gameManager.getLobby()));
		}
	}
}
