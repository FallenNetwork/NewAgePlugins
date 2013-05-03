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
	
	
	/**
	 * 
	 * Code for testing the reflections and stuff, use the isSessionValid in the AuthPlayer class instead
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	/*public static void setupReflections() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		Field field = CraftServer.class.getDeclaredField("console");
		field.setAccessible(true);
		DedicatedServer server = (DedicatedServer) field.get(NewAgeAuth.getInstance().getServer());
		field = DedicatedServer.class.getDeclaredField("r");
		field.setAccessible(true);
		DedicatedServerConnection serverConnection = (DedicatedServerConnection) field.get(server);
		field = DedicatedServerConnection.class.getDeclaredField("b");
		field.setAccessible(true);
		DedicatedServerConnectionThread connThread = (DedicatedServerConnectionThread) field.get(serverConnection);
		field = DedicatedServerConnectionThread.class.getDeclaredField("a");
		field.setAccessible(true);
		List pendingList = (List) field.get(connThread);
		for (Object connectionObject : pendingList) {
			PendingConnection connection = (PendingConnection) connectionObject;
			field = PendingConnection.class.getDeclaredField("loginKey");
			field.setAccessible(true);
			String loginKey = (String) field.get(connection);
			field = PendingConnection.class.getDeclaredField("g");
			field.setAccessible(true);
			String g = (String) field.get(connection);
			field = PendingConnection.class.getDeclaredField("server");
			field.setAccessible(true);
			MinecraftServer connServer = (MinecraftServer) field.get(connection);
			field = PendingConnection.class.getDeclaredField("k");
			field.setAccessible(true);			
			SecretKey secretKey = (SecretKey) field.get(connection);
            String s = (new BigInteger(MinecraftEncryption.a(loginKey, connServer.F().getPublic(), secretKey))).toString(16);
            URL url = new URL("http://session.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(g, "UTF-8") + "&serverId=" + URLEncoder.encode(s, "UTF-8"));
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String answer = bufferedreader.readLine();
            bufferedreader.close();
			System.out.println("interrupting pending connection");
			System.out.println(connection.getName());
			System.out.println(answer);
		}
	}*/
}
