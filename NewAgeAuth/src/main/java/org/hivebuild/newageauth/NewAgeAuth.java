package org.hivebuild.newageauth;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.hivebuild.newageauth.databases.MySQL;
import org.hivebuild.newageauth.databases.SQLite;

public class NewAgeAuth extends JavaPlugin {

	private static IOManager io;
	private static Configuration config;
	private static Database db;
	private static JavaPlugin instance;
	
	@Override
	public void onLoad() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		new File(getDataFolder(), "lang").mkdirs();
		config = new Configuration();
		io = new IOManager();
		config.init();
		io.init();
		Database.registerDatabaseSystem("MySQL", new MySQL());
		Database.registerDatabaseSystem("SQLite", new SQLite());
		db = Database.getDatabaseBySystem(config.getString("Database.System"));
		if (db == null) {
			io.sendConsoleWarning("Unknown Database System. Falling back to SQLite");
			db = Database.getDatabaseBySystem("SQLite");
		}
		db.init();
		if (config.getDebug()) io.sendConsoleWarning("NewAgeAuth is running in Debug Mode");
		getServer().getPluginManager().registerEvents(new LoginHandler(), this);
	}
		
	public static IOManager getIOManager() {
		return io;
	}
	
	public static Configuration getConfiguration() {
		return config;
	}
	
	public static Database getDb() {
		return db;
	}
	
	public static JavaPlugin getInstance() {
		return instance;
	}	
}
