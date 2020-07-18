package me.didi.utils.voting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.didi.BWMain;
import me.didi.utils.ItemBuilder;

public class Voting
{

	public static final int MAP_AMOUNT = 2;
	public static final String VOTING_INVENTORY_STRING = "§6Wähle eine Map!";
	private BWMain plugin;
	private ArrayList<Map> maps;
	private Map[] votingMaps;
	private HashMap<String, Integer> playerVotes;
	private Inventory votingInventory;
	private int[] votingInventoryOrder = new int[] { 3, 5 };

	public Voting(BWMain plugin, ArrayList<Map> maps)
	{
		this.plugin = plugin;
		this.maps = maps;
		this.votingMaps = new Map[MAP_AMOUNT];
		this.playerVotes = new HashMap<String, Integer>();

		chooseRandomMaps();
		initVotingInventory();
	}

	private void chooseRandomMaps()
	{
		for (int i = 0; i < this.votingMaps.length; i++)
		{
			Collections.shuffle(maps);
			votingMaps[i] = maps.remove(0);
		}
	}

	public void initVotingInventory()
	{
		votingInventory = Bukkit.createInventory(null, 9, Voting.VOTING_INVENTORY_STRING);
		Bukkit.getConsoleSender().sendMessage("HIIII!!!");

		for (int i = 0; i < votingMaps.length; i++)
		{
			Map currentMap = votingMaps[i];
			votingInventory.setItem(votingInventoryOrder[i],
					new ItemBuilder(Material.PAPER).setDisplayName(currentMap.getName())
							.setLore("§6" + currentMap.getName() + " §c - §c§l" + currentMap.getVotes(),
									"§7Builder: " + currentMap.getBuilder())
							.build());
		}

	}

	public Map getWinnerMap()
	{
		Map winnerMap = votingMaps[0];
		for (int i = 1; i < votingMaps.length; i++)
		{
			if (votingMaps[i].getVotes() >= winnerMap.getVotes())
			{
				winnerMap = votingMaps[i];
			}
		}

		return winnerMap;
	}

	public HashMap<String, Integer> getPlayerVotes()
	{
		return playerVotes;
	}

	public void vote(Player p, int votingMap)
	{
		if (!playerVotes.containsKey(p.getName()))
		{
			votingMaps[votingMap].addVote();
			p.closeInventory();
			p.sendMessage(BWMain.prefix + "§aDu hast für die Map §6" + votingMaps[votingMap].getName() + " §agewählt!");
			playerVotes.put(p.getName(), votingMap);
			initVotingInventory();
		} else
			p.sendMessage(BWMain.prefix + "§cDu hast bereits gevotet!");
	}

	public Inventory getVotingInventory()
	{
		return votingInventory;
	}

	public int[] getVotingInventoryOrder()
	{
		return votingInventoryOrder;
	}

	public Map[] getVotingMaps()
	{
		return votingMaps;
	}

}
