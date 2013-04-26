package org.hivebuild.newageauth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

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
			event.setKickMessage(config.getString("Messages.Kick.UserAlreadyOnline"));
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
					event.setKickMessage(config.getString("Messages.Kick.Multi-Users"));
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
				event.setKickMessage(config.getString("Messages.Kick.Cracked-Premium-User"));
			}else event.setLoginResult(Result.ALLOWED);
		}else {
			
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		AuthPlayer player = players.get(event.getPlayer().getName());
		if (player.isPremium()) {
			io.send(event.getPlayer(), config.getString("Messages.Login.Premium"));
			event.getPlayer().sendMessage(config.getString("Messages.Login.Premium"));
		}else {
			if (!player.isIPValid()) {
				io.sendError(event.getPlayer(), config.getString("Messages.Kick.Cracked-Multi-IP"));
				if (config.getBoolean("Key.Enabled")) {
					io.send(event.getPlayer(), "Please enter password to proceed: ");
					//TODO: Implement Password request
				}
			}else {
				io.send(event.getPlayer(), config.getString("Messages.Login.Cracked"));
				System.out.println("Cracked user " + player.getName() + " logged in");
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		players.remove(event.getPlayer().getName());
	}
}
