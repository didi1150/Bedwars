package me.didi.utils;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class TitleAPI
{
	public static void sendTitle(Player p, String title, String subtitle, int fadein, int stay, int fadeout)
	{
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		IChatBaseComponent ititle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
		IChatBaseComponent isub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");

		PacketPlayOutTitle titletime = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, ititle, fadein,
				stay, fadeout);
		PacketPlayOutTitle subtitletime = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, isub);

		PacketPlayOutTitle titlepacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, ititle);
		PacketPlayOutTitle subpacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, isub);

		connection.sendPacket(titletime);
		connection.sendPacket(subtitletime);

		connection.sendPacket(titlepacket);
		connection.sendPacket(subpacket);
	}
}
