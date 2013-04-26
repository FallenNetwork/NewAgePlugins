package org.hivebuild.newageauth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * 
 * Loads and manages the config File
 * 
 */

public class Configuration {

	private static IOManager io;
	private static final Plugin plugin;
	private static FileConfiguration config;
	private static File configFile;
	private static String dbType = "SQLite";

	static {
		plugin = NewAgeAuth.getInstance();
	}

	/**
	 * 
	 * Loads and creates if needed the config
	 * 
	 */

	public void init() {
		io = NewAgeAuth.getIOManager();
		configFile = new File(plugin.getDataFolder(), "config.yml");
		config = plugin.getConfig();
		if (!configFile.exists()) {
			io.sendConsole("Creating config.yml...", true);
			try {
				configFile.createNewFile();
				updateConfig();
				io.sendConsole("config.yml succesfully created!", true);
				config.load(configFile);
			}catch (IOException e) {
				if (getDebug()) e.printStackTrace();
			}catch (InvalidConfigurationException e) {
				if (getDebug()) e.printStackTrace();
			}
		}else {
			try {
				config.load(configFile);
				updateConfig();
				config.load(configFile);
			}catch (IOException e) {
				if (getDebug()) e.printStackTrace();
			}catch (InvalidConfigurationException e) {
				if (getDebug()) e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * Updates an already existing config
	 * 
	 * @throws IOException
	 * 
	 */
	public void updateConfig() throws IOException {
		//TODO: Add missing settings
		update("General.Debug", false);
		update("IO.Show-Prefix", true);
		update("IO.Prefix", "&4[NewAgeAuth]");
		update("IO.Error", "&c[Error]");
		update("IO.Warning", "&e[Warning]");
		update("IO.ColoredLogs", true);
		update("Database.System", "SQLite");
		update("Messages.Login.Premium", "You have successfully logged in.");
		update("Messages.Login.Cracked", "You have been logged in. You can now only log in to this server with your current username.");
		update("Messages.Kick.UserAlreadyOnline", "A User with this username is already online");
		update("Messages.Kick.Premium-User-Session-Failed", "Login Session is incorrect, please restart minecraft and try again");
		update("Messages.Kick.Cracked-Multi-IP", "You connecting to the server with same user but mutliple IPs");
		update("Messages.Kick.Multi-Users", "You connected to the server with different minecraft accounts. Please connect with the same you have connected with before.");
		update("Messages.Kick.Cracked-Premium-User", "You connected to the server with a premium username but you are not currently logged in to that premium user. Please log in to that premium user or try a different username.");
		update("Messages.Ban.1-Key-exceede-max-attempts", "You have exceeded the max amount of key attempts");
		update("Messages.Ban.2-Key-exceede-max-attempts", "Attempts Made: %keyattemptsmade%");
		update("Messages.Ban.3-Key-exceede-max-attempts", "Attempts Allowed: %keyattempsmax%");
		update("Messages.Ban.4-Key-exceede-max-attempts", "Username: %username%");
		update("Messages.Ban.5-Key-exceede-max-attempts", "IP: %IP%");
		update("Key.Enabled", true);
		update("Key.Key-Timeout", 1);
		update("Key.Max-Key-Attempts", 5);
		update("Key.Keep-Key-Attempts", 1440);
		update("Key.Ban-on-exceeded-key-attempts", true);
		update("Key.Ban-exceeded-attempts-time", 2880);
		update("Spambot.Enabled", true);
		update("Spambot.max-failed-login-attempts", 5);
		update("Spambot.Login-wait-time", 1);
		update("Spambot.Lock-Server", true);
		update("Spambot.Lock-Server-Time", 5);
		update("Spambot.Ban-IPs-associated", true);
		update("Ban.Ban-Command", "ban");
		update("Ban.Pardon-Command", "pardon");
		config.save(configFile);
	}


	/**
	 * 
	 * Return whether NewAgeAuth is in Debug Mode or not
	 * 
	 * @return The Debug Mode
	 * 
	 */
	public boolean getDebug() {
		return config.getBoolean("General.Debug", false);
	}

	/**
	 * 
	 * @return The currently used Database System
	 * @see org.hivebuild.newageauth.Database
	 */

	public String getDatabaseType() {
		return dbType;
	}

	public String getString(String path) {
		return config.getString(path);
	}

	public String getString(String path, String def) {
		return config.getString(path, def);
	}

	public boolean getBoolean(String path) {
		return config.getBoolean(path);
	}

	public boolean getBoolean(String path, Boolean def) {
		return config.getBoolean(path, def);
	}

	public int getInt(String path) {
		return config.getInt(path);
	}

	public int getInt(String path, int def) {
		return config.getInt(path, def);
	}

	public List<?> getList(String path) {
		return config.getList(path);
	}

	public List<?> getList(String path, List<?> def) {
		return config.getList(path, def);
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	public List<Integer> getIntegerList(String path) {
		return config.getIntegerList(path);
	}

	public Object get(String path) {
		return config.get(path);
	}

	public Object get(String path, Object def) {
		return config.get(path, def);
	}

	public boolean update(String path, Object value) {
		if (!config.contains(path)) {
			config.set(path, value);
			return false;
		}else return true;
	}

	public void set(String path, Object value) {
		config.set(path, value);
	}

	public void remove(String path) {
		config.set(path, null);
	}

	public boolean contains(String path) {
		return config.contains(path);
	}

	public void reload() {
		try {
			config.load(configFile);
		}catch (FileNotFoundException e) {
			if (getDebug()) e.printStackTrace();
		}catch (IOException e) {
			if (getDebug()) e.printStackTrace();
		}catch (InvalidConfigurationException e) {
			if (getDebug()) e.printStackTrace();
		}
	}

	public void save() {
		try {
			config.save(configFile);
		}catch (IOException e) {
			if (getDebug()) e.printStackTrace();
		}
	}
}
