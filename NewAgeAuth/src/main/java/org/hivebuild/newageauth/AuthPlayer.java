package org.hivebuild.newageauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.crypto.SecretKey;

import net.minecraft.server.v1_5_R3.DedicatedServer;
import net.minecraft.server.v1_5_R3.DedicatedServerConnection;
import net.minecraft.server.v1_5_R3.DedicatedServerConnectionThread;
import net.minecraft.server.v1_5_R3.MinecraftEncryption;
import net.minecraft.server.v1_5_R3.MinecraftServer;
import net.minecraft.server.v1_5_R3.PendingConnection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.Player;

public class AuthPlayer {

	private String ip, key;
	private final String name, lastIP;
	private boolean cracked = false, premium = false;
	private Player player;
	
	private static Configuration config;
	private static Database db;
	
	static {
		config = NewAgeAuth.getConfiguration();
		db = NewAgeAuth.getDb();
	}
	
	public AuthPlayer(String name) {
		this(name, Bukkit.getPlayer(name).getAddress().getAddress().getHostAddress());
	}
	
	public AuthPlayer(String name, String ip) {
		this.name = name;
		if (db.tableContains("player", "name", name)) {
			String tempIP = db.queryString("SELECT `ip` FROM `player` WHERE `name`='" + name + "';", "ip");
			if (tempIP != null) lastIP = tempIP;
			else {
				lastIP = ip;
				db.update("UPDATE `player` SET `ip`='" + ip + "' WHERE `name`='" + name + "';");
			}
		}else {
			db.update("INSERT INTO `player` VALUES ('" + name + "', '" + ip + "', NULL);");
			lastIP = ip;
		}
		premium = isPlayerPremium(name);
		cracked = !isSessionValid();
		System.out.println(cracked);
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIP() {
		return ip;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public boolean isIPValid() {
		if (lastIP == ip) return true;
		else return false;
	}
	
	public boolean isPremium() {
		return premium;
	}
	
	public boolean isCracked() {
		return cracked;
	}
	
	public static boolean isPlayerPremium(String name) {
		boolean result = false;
		try {
			URL url = new URL("http://minecraft.net/haspaid.jsp?user=" + name);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			if (in.readLine().equalsIgnoreCase("true")) result = true;
			else result = false;
			in.close();
		}catch(IOException e) {
			if (config.getDebug()) e.printStackTrace();
		}
        return result;
	}
	
	public boolean isSessionValid() {
		try {
			//Get the Server Instance
			Field field = CraftServer.class.getDeclaredField("console");
			field.setAccessible(true);
			DedicatedServer server = (DedicatedServer) field.get(NewAgeAuth.getInstance().getServer());
			//Get the DedicatedServerConnection Instance
			field = DedicatedServer.class.getDeclaredField("r");
			field.setAccessible(true);
			DedicatedServerConnection serverConnection = (DedicatedServerConnection) field.get(server);
			//Get the DedicatedServerConnectionThread Instance
			field = DedicatedServerConnection.class.getDeclaredField("b");
			field.setAccessible(true);
			DedicatedServerConnectionThread connThread = (DedicatedServerConnectionThread) field.get(serverConnection);
			//Get the List which contains all PendingConnections
			field = DedicatedServerConnectionThread.class.getDeclaredField("a");
			field.setAccessible(true);
			List pendingList = (List) field.get(connThread);
			//Go through the list (only handling first entry)
			for (Object connectionObject : pendingList) {
				//Convert object into PendingConnection
				PendingConnection connection = (PendingConnection) connectionObject;
				//Get the LoginKey
				field = PendingConnection.class.getDeclaredField("loginKey");
				field.setAccessible(true);
				String loginKey = (String) field.get(connection);
				//Get the Username
				field = PendingConnection.class.getDeclaredField("g");
				field.setAccessible(true);
				String username = (String) field.get(connection);
				//Get the Minecraftserver (maybe same as above works?)
				field = PendingConnection.class.getDeclaredField("server");
				field.setAccessible(true);
				MinecraftServer connServer = (MinecraftServer) field.get(connection);
				//Get the SecretKey
				field = PendingConnection.class.getDeclaredField("k");
				field.setAccessible(true);			
				SecretKey secretKey = (SecretKey) field.get(connection);
				//generate the Server ID
				String s = (new BigInteger(MinecraftEncryption.a(loginKey, connServer.F().getPublic(), secretKey))).toString(16);
				//Check the Server
				URL url = new URL("http://session.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(username, "UTF-8") + "&serverId=" + URLEncoder.encode(s, "UTF-8"));
				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
				String answer = bufferedreader.readLine();
				bufferedreader.close();
				if (answer.equalsIgnoreCase("YES")) return true;
				else return false;
			}
		}catch (Exception e) {
			if (config.getDebug()) e.printStackTrace();
		}
		return true;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isUsernameValid() {
		
		return false;
	}
	
	public void hide() {
		Player[] players = NewAgeAuth.getInstance().getServer().getOnlinePlayers();
		for (Player player : players) player.hidePlayer(this.player);
	}
	
	public void show() {
		Player[] players = NewAgeAuth.getInstance().getServer().getOnlinePlayers();
		for (Player player : players) player.showPlayer(this.player);
	}
	
	public void queryKey() {
		//TODO: Implement the key request
	}
}
