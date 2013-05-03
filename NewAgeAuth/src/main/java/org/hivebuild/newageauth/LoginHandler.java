package org.hivebuild.newageauth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginHandler implements Listener {

	private static final IOManager io;
	private static final Configuration config;
	private static final Database db;
	private static final HashMap<String, AuthPlayer> players;
	
	static {
		players = new HashMap<String, AuthPlayer>();
		config = NewAgeAuth.getConfiguration();
		io = NewAgeAuth.getIOManager();
		db = NewAgeAuth.getDb();
	}
	
	@EventHandler
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		System.out.println("onPlayerPreLogin-1");
		if (players.containsKey(event.getName())) {
			event.disallow(Result.KICK_OTHER, io.translate("Kick.AlreadyOnline"));
			return;
		}
		try {
			ResultSet rs = db.query("SELECT * FROM `player` WHERE `ip`='" + event.getAddress() + "';");
			if (rs != null) {
				int i = 0;
				while (rs.next()) {
					if (rs.getString("name") != event.getName()) i++;
				}
				if (i > 0) {
					event.disallow(Result.KICK_OTHER, io.translate("Kick.CrackedMultiUsers"));
				}
			}
		}catch(SQLException e) {
			if (config.getDebug()) e.printStackTrace();
		}
		System.out.println("onPlayerPreLogin-2");
		System.out.println(event.getName());
		AuthPlayer player = new AuthPlayer(event.getName(), event.getAddress().getHostAddress());
		players.put(player.getName(), player);
		if (player.isPremium()) {
			if (player.isCracked()) event.disallow(Result.KICK_OTHER, io.translate("Kick.CrackedPremiumUser"));
		}else {
			if (!player.isIPValid() && !config.getBoolean("Key.Enabled")) event.disallow(Result.KICK_OTHER, io.translate("Kick.CrackedMultiIP"));
			if (!player.isUsernameValid() && !config.getBoolean("Key.Enabled")) event.disallow(Result.KICK_OTHER, io.translate("Kick.CrackedMultiUsers"));				
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		System.out.println(event.getPlayer().getName());
		AuthPlayer player = players.get(event.getPlayer().getName());
		System.out.println(player);
		player.setPlayer(event.getPlayer());
		if (player.isPremium()) {
			io.send(event.getPlayer(), io.translate("Login.Premium"));
		}else {
			if (!player.isIPValid()) {
				player.hide();
				event.setJoinMessage(null);
				io.sendError(event.getPlayer(), io.translate("Kick.CrackedMultiIP"));
				io.send(event.getPlayer(), io.translate("Login.Key"));
				player.queryKey();
			}if (!player.isUsernameValid()) {
				player.hide();
				event.setJoinMessage(null);
				io.sendError(event.getPlayer(), io.translate("Kick.CrackedMultiUsers"));
				io.send(event.getPlayer(), io.translate("Login.Key"));
				player.queryKey();
			}else io.send(event.getPlayer(), io.translate("Login.Cracked"));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		players.remove(event.getPlayer().getName());
	}
}
