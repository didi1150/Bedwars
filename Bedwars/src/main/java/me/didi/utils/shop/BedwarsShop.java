package me.didi.utils.shop;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;

import com.mojang.authlib.GameProfile;

import me.didi.BWMain;
import me.didi.utils.GameManager;
import me.didi.utils.GameTeam;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

public class BedwarsShop extends Reflections
{

	GameTeam team;
	int entityID;
	Location location;
	BWMain plugin;
	GameManager gameManager;
	GameProfile gameProfile;

	public BedwarsShop(GameTeam team, BWMain plugin, String name, Location loc)
	{
		this.team = team;
		this.plugin = plugin;
		this.gameManager = plugin.getGameManager();
		this.location = loc;
		this.entityID = (int) Math.ceil(Math.random() * 1000) + 2000;
		gameProfile = new GameProfile(UUID.randomUUID(), name);
	}

	public void spawn()
	{
		PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();
		setValue(packetPlayOutNamedEntitySpawn, "a", entityID);
		setValue(packetPlayOutNamedEntitySpawn, "b", gameProfile.getId());
		setValue(packetPlayOutNamedEntitySpawn, "c", (int) MathHelper.floor(location.getX() * 32.0D));
		setValue(packetPlayOutNamedEntitySpawn, "d", (int) MathHelper.floor(location.getY() * 32.0D));
		setValue(packetPlayOutNamedEntitySpawn, "e", (int) MathHelper.floor(location.getZ() * 32.0D));
		setValue(packetPlayOutNamedEntitySpawn, "f", (byte) ((int) location.getYaw() * 256.0F / 360.0F));
		setValue(packetPlayOutNamedEntitySpawn, "g", (byte) ((int) location.getPitch() * 256.0F / 360.0F));
		setValue(packetPlayOutNamedEntitySpawn, "h", 0);
		DataWatcher w = new DataWatcher(null);
		w.a(6, (float) 20);
		w.a(10, (byte) 127);
		setValue(packetPlayOutNamedEntitySpawn, "i", w);
		addToTablist();
		sendPacket(packetPlayOutNamedEntitySpawn);
	}

	public void destroy()
	{
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { entityID });
		removeFromTablist();
		sendPacket(packet);
	}

	public void addToTablist()
	{
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, 1, EnumGamemode.NOT_SET,
				CraftChatMessage.fromString(gameProfile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(
				packet, "b");
		players.add(data);
		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
		setValue(packet, "n", players);

		sendPacket(packet);
	}

	public void removeFromTablist()
	{
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, 1, EnumGamemode.NOT_SET,
				CraftChatMessage.fromString("")[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(
				packet, "b");
		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
		setValue(packet, "n", players);

		sendPacket(packet);
	}

}
