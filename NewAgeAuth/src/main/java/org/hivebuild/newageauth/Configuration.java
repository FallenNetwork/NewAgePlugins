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
		update("General.Debug", false);
		update("General.Log-IPs", "all");
		update("General.MaxCrackedUsersPerIP", 1);
		update("General.AllowCracked", true);
		update("General.MessageOnPremiumLogin", false);
		update("General.MessageOnCrackedLogin", true);
		
		update("IO.Show-Prefix", true);
		update("IO.Prefix", "&4[NewAgeAuth]");
		update("IO.Error", "&c[Error]");
		update("IO.Warning", "&e[Warning]");
		update("IO.ColoredLogs", true);
		
		update("Database.System", "SQLite");
		
		update("Key.Enabled", true);
		update("Key.KeyTimeout", 1);
		update("Key.MaxKeyAttempts", 5);
		update("Key.KeepKeyAttempts", 1440);
		update("Key.BanOnExceededKeyAttempts", true);
		update("Key.BanExceededAttemptsTime", 2880);
		
		update("Spambot.Enabled", true);
		update("Spambot.MaxFailedLoginAttempts", 5);
		update("Spambot.LoginWaitTime", 1);
		update("Spambot.LockServer", true);
		update("Spambot.LockServerTime", 5);
		update("Spambot.BanIPsAssociated", true);
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
