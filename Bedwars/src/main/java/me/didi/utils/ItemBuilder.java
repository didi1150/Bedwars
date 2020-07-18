package me.didi.utils;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder
{

	private ItemStack is;
	private ItemMeta im;

	public ItemBuilder(Material material)
	{
		is = new ItemStack(material);
		im = is.getItemMeta();
	}

	public ItemBuilder setDisplayName(String name)
	{
		im.setDisplayName(name);
		return this;
	}

	public ItemBuilder setLore(String... lore)
	{
		im.setLore(Arrays.asList(lore));
		return this;
	}

	public ItemStack build()
	{
		is.setItemMeta(im);
		return is;
	}

}
