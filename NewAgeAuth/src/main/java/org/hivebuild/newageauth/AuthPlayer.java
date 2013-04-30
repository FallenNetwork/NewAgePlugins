package org.hivebuild.newageauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
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
		cracked = !isSessionValid(name);
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
	
	public static boolean isSessionValid(String name) {
		//TODO: Implement a way to check the session
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
		//TODO: Hide Player from the Public
	}
	
	public void show() {
		//TODO: Show Player to the Public
	}
	
	public boolean queryKey() {
		//TODO: Implement the key request
		return true;
	}
}
