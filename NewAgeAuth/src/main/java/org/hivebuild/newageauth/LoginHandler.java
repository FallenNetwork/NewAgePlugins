package org.hivebuild.newageauth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginHandler implements Listener {

	private static final IOManager io;
	private static final Configuration config;
	private static final Database db;
	private final HashMap<String, AuthPlayer> players;
	
	static {
		config = NewAgeAuth.getConfiguration();
		io = NewAgeAuth.getIOManager();
		db = NewAgeAuth.getDb();
	}
	
	{
		players = new HashMap<String, AuthPlayer>();
	}
	
	@EventHandler
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (players.containsKey(event.getName())) {
			event.setLoginResult(Result.KICK_OTHER);
			event.setKickMessage(io.translate("Kick.AlreadyOnline"));
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
					event.setLoginResult(Result.KICK_OTHER);
					event.setKickMessage(io.translate("Kick.CrackedMultiUsers"));
					return;
				}
			}
		}catch(SQLException e) {
			if (config.getDebug()) e.printStackTrace();
		}
		AuthPlayer player = new AuthPlayer(event.getName(), event.getAddress().getHostAddress());
		players.put(player.getName(), player);
		if (player.isPremium()) {
			if (player.isCracked()) {
				event.setLoginResult(Result.KICK_OTHER);
				event.setKickMessage(io.translate("Kick.CrackedPremiumUser"));
			}else event.setLoginResult(Result.ALLOWED);
		}else {
			if (!player.isIPValid() && !config.getBoolean("Key.Enabled")) {
				event.setLoginResult(Result.KICK_OTHER);
				event.setKickMessage(io.translate("Kick.CrackedMultiIP"));
			}
			if (!player.isUsernameValid() && !config.getBoolean("Key.Enabled")) {				
				event.setLoginResult(Result.KICK_OTHER);
				event.setKickMessage(io.translate("Kick.CrackedMultiUsers"));
			}
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		AuthPlayer player = players.get(event.getPlayer().getName());
		if (player.isPremium()) {
			io.send(event.getPlayer(), io.translate("Login.Premium"));
		}else {
			if (!player.isIPValid()) {
				//TODO: Hide Player from the Public
				io.sendError(event.getPlayer(), io.translate("Kick.CrackedMultiIP"));
				io.send(event.getPlayer(), io.translate("Login.Key"));
				if (queryKey(event.getPlayer())) {
					//TODO: Show Player to the Public
				}
			}if (!player.isUsernameValid()) {
				//TODO: Hide Player from the Public
				io.sendError(event.getPlayer(), io.translate("Kick.CrackedMultiUsers"));
				io.send(event.getPlayer(), io.translate("Login.Key"));
				if (queryKey(event.getPlayer())) {
					//TODO: Show Player to the Public
				}
			}else {
				io.send(event.getPlayer(), io.translate("Login.Cracked"));
			}
		}
	}
	
	private boolean queryKey(Player player) {
		//TODO: Implement a Key Request
		return false;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		players.remove(event.getPlayer().getName());
	}
}
