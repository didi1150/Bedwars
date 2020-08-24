package me.didi.utils.shop;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;

public class PacketReader
{

	Player player;
	Channel channel;

	public PacketReader(Player player)
	{
		this.player = player;
	}

	// Injiziert den neuen Decoder nach dem alten Decoder in die Channel-Klasse des
	// Spielers
	public void inject()
	{
		// Castet den Spieler zum CraftPlayer
		CraftPlayer player = (CraftPlayer) this.player;
		// Holt die Channel-Klasse von dem Spieler
		channel = player.getHandle().playerConnection.networkManager.channel;
		// Fügt den neuen Decoder nach dem standart Decoder hinzu
		channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>()
		{
			@Override
			protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2) throws Exception
			{
				arg2.add(packet);
				readPackets(packet);
			}
		});
	}

	// Entfernt den neuen Decoder aus der Channel-Klasse des Spielers
	public void uninject()
	{
		// Fragt ob es den neuen Decoder gibt
		if (channel.pipeline().get("PacketInjector") != null)
		{
			// Entfernt den neuen Decoder
			channel.pipeline().remove("PacketInjector");
		}
	}

	// Packets auslesen
	private void readPackets(Packet<?> packet)
	{
		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity"))
		{
			int id = (int) getValue(packet, "a");
			String use = getValue(packet, "action").toString();
		}
	}

	public void setValue(Object obj, String name, Object value)
	{
		Field field;
		try
		{
			field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e)
		{
			e.printStackTrace();
		}

	}

	public Object getValue(Object obj, String name)
	{
		Field field;
		try
		{
			field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
