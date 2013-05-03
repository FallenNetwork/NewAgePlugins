package org.hivebuild.newageauth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
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
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (players.containsKey(event.getPlayer().getName())) {
			event.disallow(Result.KICK_OTHER, io.translate("Kick.AlreadyOnline"));
			return;
		}
		try {
			ResultSet rs = db.query("SELECT * FROM `player` WHERE `ip`='" + event.getAddress() + "';");
			if (rs != null) {
				int i = 0;
				while (rs.next()) {
					if (rs.getString("name") != event.getPlayer().getName()) i++;
				}
				if (i > 0) {
					event.disallow(Result.KICK_OTHER, io.translate("Kick.CrackedMultiUsers"));
				}
			}
		}catch(SQLException e) {
			if (config.getDebug()) e.printStackTrace();
		}
		AuthPlayer player = new AuthPlayer(event.getPlayer().getName(), event.getAddress().getHostAddress());
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
